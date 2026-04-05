<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="我的商品" />

      <section class="section">
        <el-button type="primary" @click="goPublish">发布商品</el-button>
      </section>

      <section class="section">
        <div class="toolbar">
          <el-select
            v-model="status"
            placeholder="商品状态"
            class="w-180"
            clearable
            @change="loadList"
          >
            <el-option label="全部" :value="''" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="草稿" value="DRAFT" />
            <el-option label="驳回" value="REJECTED" />
            <el-option label="下架" value="OFFLINE" />
            <el-option label="已卖出" value="SOLD" />
          </el-select>

          <el-input
            v-model="keyword"
            placeholder="搜索商品标题"
            class="keyword-input"
            clearable
            @keyup.enter="loadList"
            @clear="loadList"
          >
            <template #append>
              <el-button @click="loadList">搜索</el-button>
            </template>
          </el-input>

          <el-button @click="loadList">刷新</el-button>
        </div>
      </section>

      <section class="section">
        <div class="table">
          <div class="header-row">
            <span>标题</span>
            <span>价格</span>
            <span>状态</span>
            <span>发布时间</span>
            <span>浏览/收藏/咨询</span>
            <span>操作</span>
          </div>
          <div
            class="data-row"
            v-for="item in list"
            :key="item.id"
          >
            <div class="title-cell">
              <img :src="item.coverUrl" alt="" />
              <span>{{ item.title }}</span>
            </div>
            <div>￥{{ item.price }}</div>
            <div>{{ statusLabel(item.status) }}</div>
            <div>{{ item.createdAt }}</div>
            <div>{{ item.viewCount }} / {{ item.favoriteCount }} / {{ item.consultCount }}</div>
            <div class="actions">
              <div class="action-main">
                <el-button size="small" type="primary" text @click="editItem(item)">编辑</el-button>
                <el-button size="small" type="warning" text @click="offlineItem(item)">下架</el-button>
                <el-button size="small" type="danger" text @click="deleteItem(item)">删除</el-button>
              </div>
              <div class="action-sub">
                <el-button size="small" text @click="republishItem(item)">重新发布</el-button>
                <el-button size="small" text @click="showStats(item)">查看数据</el-button>
                <el-button size="small" text @click="showConsult(item)">查看咨询</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination">
          <el-pagination
            background
            layout="total, prev, pager, next"
            :total="total"
            :current-page="pageNum"
            :page-size="pageSize"
            @current-change="
              (p) => {
                pageNum = p;
                loadList();
              }
            "
          />
        </div>
      </section>

      <section class="section">
        <el-button @click="goBack">返回个人中心</el-button>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';

const router = useRouter();

const status = ref('');
const keyword = ref('');
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 10;

function statusLabel(s) {
  switch (s) {
    case 'PUBLISHED':
      return '已发布';
    case 'DRAFT':
      return '草稿';
    case 'REJECTED':
      return '驳回';
    case 'OFFLINE':
      return '下架';
    case 'SOLD':
      return '已卖出';
    default:
      return s || '-';
  }
}

async function loadList() {
  const res = await request.get('/seller/products', {
    params: {
      status: status.value,
      keyword: keyword.value,
      pageNum: pageNum.value,
      pageSize,
    },
  });
  const page = res.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
}

function goPublish() {
  router.push({ name: 'ProductPublish' });
}

function goBack() {
  router.push({ name: 'ProfileHome' });
}

function editItem(item) {
  router.push({ name: 'ProductPublish', query: { id: item.id } });
}

function offlineItem(item) {
  if (item.status === 'OFFLINE') {
    ElMessage.info('该商品已是下架状态');
    return;
  }
  ElMessageBox.confirm('确认将该商品下架吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.post(`/seller/products/${item.id}/status`, null, { params: { status: 'OFFLINE' } });
      ElMessage.success('已下架');
      loadList();
    })
    .catch(() => {});
}

function deleteItem(item) {
  ElMessageBox.confirm('确认删除该商品吗？删除后无法恢复。', '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/seller/products/${item.id}`);
      ElMessage.success('已删除');
      loadList();
    })
    .catch(() => {});
}

function republishItem(item) {
  ElMessageBox.confirm('确认重新发布该商品并提交审核吗？', '提示', { type: 'info' })
    .then(async () => {
      await request.post(`/seller/products/${item.id}/status`, null, { params: { status: 'PENDING' } });
      ElMessage.success('已重新提交审核');
      loadList();
    })
    .catch(() => {});
}

function showStats(item) {
  ElMessage.info(`浏览 ${item.viewCount} · 收藏 ${item.favoriteCount} · 咨询 ${item.consultCount}`);
}

function showConsult() {
  ElMessage.info('咨询列表功能稍后接入消息模块');
}

onMounted(() => {
  loadList();
});
</script>

<style scoped>
.main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1rem;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
}

.keyword-input {
  flex: 1 1 260px;
}

.w-180 {
  width: 180px;
}

.table {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.75rem 0.75rem 0.25rem;
}

.header-row,
.data-row {
  display: grid;
  grid-template-columns: minmax(0, 2.2fr) 0.8fr 0.8fr 1.2fr 1.4fr 2fr;
  gap: 0.75rem;
  padding: 0.35rem 0;
  align-items: center;
  font-size: 0.9rem;
}

.header-row {
  font-weight: 600;
  border-bottom: 1px solid #e5e7eb;
}

.data-row {
  border-bottom: 1px solid #f3f4f6;
}

.data-row:last-child {
  border-bottom: none;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.title-cell img {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 8px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-start;
}

.action-main {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.action-sub {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  font-size: 12px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.75rem;
}
</style>

