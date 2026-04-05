<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="公告中心" subtitle="平台通知与公告" />
      <el-card shadow="never" class="card">
        <div class="toolbar">
          <el-select v-model="type" placeholder="类型" clearable class="w-160" @change="loadData">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="活动" value="ACTIVITY" />
            <el-option label="规则" value="RULE" />
          </el-select>
          <el-input
            v-model="keyword"
            placeholder="搜索标题/内容"
            clearable
            class="keyword"
            @keyup.enter="loadData"
            @clear="loadData"
          >
            <template #append>
              <el-button @click="loadData">搜索</el-button>
            </template>
          </el-input>
          <el-button @click="loadData">刷新</el-button>
        </div>

        <el-empty v-if="!list.length" description="暂无公告" />
        <div v-else class="list">
          <article
            v-for="item in list"
            :key="item.id"
            class="notice"
            @click="openDetail(item)"
          >
            <div class="title-row">
              <span class="title">
                <el-tag v-if="item.pinned" size="small" type="danger" effect="light">置顶</el-tag>
                {{ item.title }}
              </span>
              <span class="time">{{ item.createdAt }}</span>
            </div>
            <div class="meta">
              <el-tag size="small" effect="light">{{ typeLabel(item.type) }}</el-tag>
            </div>
            <div class="preview">{{ stripHtml(item.content).slice(0, 120) }}...</div>
          </article>
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
                loadData();
              }
            "
          />
        </div>
      </el-card>
    </main>

    <el-dialog v-model="detailVisible" :title="current?.title || '公告详情'" width="760px">
      <div class="detail-meta">
        <el-tag size="small" effect="light">{{ typeLabel(current?.type) }}</el-tag>
        <span class="detail-time">{{ current?.createdAt }}</span>
      </div>
      <div class="detail-content" v-html="current?.content"></div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';

const type = ref('');
const keyword = ref('');
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 10;

const detailVisible = ref(false);
const current = ref(null);

function typeLabel(t) {
  if (t === 'ACTIVITY') return '活动';
  if (t === 'RULE') return '规则';
  return '系统通知';
}

function stripHtml(html) {
  if (!html) return '';
  return String(html).replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim();
}

async function loadData() {
  const res = await request.get('/notice', {
    params: { type: type.value, keyword: keyword.value, pageNum: pageNum.value, pageSize },
  });
  const page = res?.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
}

async function openDetail(item) {
  if (!item?.id) return;
  const res = await request.get(`/notice/${item.id}`);
  current.value = res?.data || item;
  detailVisible.value = true;
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.main {
  max-width: 900px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.card {
  border-radius: 12px;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
  margin-bottom: 0.75rem;
}

.w-160 {
  width: 160px;
}

.keyword {
  flex: 1 1 260px;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.notice {
  border: 1px solid #e5e7eb;
  background: #ffffff;
  border-radius: 12px;
  padding: 0.85rem 1rem;
  cursor: pointer;
  transition: box-shadow 0.15s ease, border-color 0.15s ease, transform 0.15s ease;
}

.notice:hover {
  border-color: #cbd5e1;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
  transform: translateY(-1px);
}

.title-row {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  align-items: center;
}

.title {
  font-weight: 600;
  color: #111827;
  display: inline-flex;
  gap: 0.5rem;
  align-items: center;
}

.time {
  font-size: 0.85rem;
  color: #6b7280;
  white-space: nowrap;
}

.meta {
  margin-top: 0.35rem;
}

.preview {
  margin-top: 0.5rem;
  color: #4b5563;
  font-size: 0.92rem;
  line-height: 1.5;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.75rem;
}

.detail-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.75rem;
  color: #6b7280;
}

.detail-content {
  line-height: 1.8;
  color: #111827;
}
</style>

