<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="浏览记录" />

      <section class="section">
        <div class="top-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索浏览过的商品"
            class="keyword-input"
            clearable
            @keyup.enter="loadList"
            @clear="loadList"
          >
            <template #append>
              <el-button @click="loadList">搜索</el-button>
            </template>
          </el-input>

          <el-button type="danger" plain @click="clearAll">清空全部记录</el-button>
        </div>
      </section>

      <section class="section">
        <div class="list-area">
          <div class="record" v-for="item in list" :key="item.id">
            <div class="left">
              <img :src="item.coverUrl" :alt="item.title" />
            </div>
            <div class="middle">
              <div class="title">{{ item.title }}</div>
              <div class="meta">
                <span>价格：￥{{ item.price }}</span>
                <span v-if="item.conditionText">{{ item.conditionText }}</span>
                <span v-if="item.campusName">校园：{{ item.campusName }}</span>
                <span>浏览时间：{{ item.viewedAt }}</span>
              </div>
            </div>
            <div class="right">
              <el-button size="small" @click="addFavorite(item.id)">加入收藏</el-button>
              <el-button size="small" @click="goDetail(item.productId)">查看详情</el-button>
              <el-button size="small" type="primary">立即购买</el-button>
              <el-button size="small" type="danger" text @click="removeOne(item.id)">删除记录</el-button>
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

const keyword = ref('');
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 12;

async function loadList() {
  const res = await request.get('/history/list', {
    params: { keyword: keyword.value, pageNum: pageNum.value, pageSize },
  });
  const page = res.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
}

function clearAll() {
  ElMessageBox.confirm('确认清空全部浏览记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete('/history/clear');
        ElMessage.success('已清空浏览记录');
        loadList();
      } catch (e) {
        ElMessage.warning(e?.response?.data?.message || '清空失败，请先登录');
      }
    })
    .catch(() => {});
}

async function removeOne(id) {
  await request.delete(`/history/${id}`);
  ElMessage.success('已删除该条记录');
  loadList();
}

async function addFavorite(id) {
  try {
    await request.post(`/history/${id}/favorite`);
    ElMessage.success('已加入收藏');
  } catch (e) {
    ElMessage.warning(e?.response?.data?.message || '加入收藏失败，请先登录');
  }
}

function goDetail(productId) {
  router.push({ name: 'ProductDetail', params: { id: productId } });
}

function goBack() {
  router.push({ name: 'ProfileHome' });
}

onMounted(() => {
  loadList();
});
</script>

<style scoped>
.main {
  max-width: 1040px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1rem;
}

.top-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
}

.keyword-input {
  flex: 1 1 260px;
}

.list-area {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.75rem 0.75rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.record {
  display: grid;
  grid-template-columns: 120px minmax(0, 1.5fr) auto;
  gap: 0.75rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.record:last-child {
  border-bottom: none;
}

.left img {
  width: 100%;
  height: 90px;
  object-fit: cover;
  border-radius: 8px;
}

.title {
  font-size: 0.98rem;
  font-weight: 500;
  margin-bottom: 0.35rem;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem 1rem;
  font-size: 0.85rem;
  color: #4b5563;
}

.right {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  align-items: flex-end;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>

