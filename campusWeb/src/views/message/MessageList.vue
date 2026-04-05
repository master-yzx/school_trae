<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="站内消息" />

      <section class="section">
        <div class="toolbar">
          <el-select
            v-model="type"
            placeholder="消息类型"
            class="w-180"
            clearable
            @change="loadList"
          >
            <el-option label="全部" :value="''" />
            <el-option label="审核提醒" value="AUDIT" />
            <el-option label="订单通知" value="ORDER" />
            <el-option label="咨询消息" value="CHAT" />
            <el-option label="系统通知" value="SYSTEM" />
          </el-select>

          <el-select
            v-model="read"
            placeholder="已读状态"
            class="w-140"
            clearable
            @change="loadList"
          >
            <el-option label="全部" :value="null" />
            <el-option label="未读" :value="false" />
            <el-option label="已读" :value="true" />
          </el-select>

          <el-button @click="loadList">刷新</el-button>
        </div>
      </section>

      <section class="section">
        <div class="actions">
          <el-button @click="batchMarkRead">全部标记已读</el-button>
          <el-button type="danger" plain @click="clearRead">清空已读</el-button>
        </div>

        <div class="list">
          <article
            v-for="item in list"
            :key="item.id"
            class="msg"
            :class="{ unread: !item.read }"
          >
            <div class="msg-header">
              <span class="tag">{{ typeLabel(item.type) }}</span>
              <span class="title">{{ item.title }}</span>
              <span class="time">{{ item.createdAt }}</span>
            </div>
            <div class="content">
              {{ item.content }}
            </div>
            <div class="msg-actions">
              <el-button
                v-if="!item.read"
                size="small"
                @click="markRead(item.id)"
              >标记已读</el-button>
              <el-button
                size="small"
                type="danger"
                text
                @click="removeOne(item.id)"
              >删除</el-button>
            </div>
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
              (v) => {
                pageNum = v;
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

const type = ref('');
const read = ref(null);
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 10;

function typeLabel(t) {
  switch (t) {
    case 'AUDIT':
      return '审核提醒';
    case 'ORDER':
      return '订单通知';
    case 'CHAT':
      return '咨询消息';
    case 'SYSTEM':
      return '系统通知';
    default:
      return '其他';
  }
}

async function loadList() {
  const res = await request.get('/messages', {
    params: {
      type: type.value,
      read: read.value,
      pageNum: pageNum.value,
      pageSize,
    },
  });
  const page = res.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
}

async function markRead(id) {
  await request.post(`/messages/${id}/read`);
  ElMessage.success('已标记为已读');
  loadList();
}

async function removeOne(id) {
  await request.delete(`/messages/${id}`);
  ElMessage.success('已删除该消息');
  loadList();
}

function batchMarkRead() {
  ElMessageBox.confirm('确认将所有消息标记为已读吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.post('/messages/batch/read');
      ElMessage.success('已全部标记为已读');
      loadList();
    })
    .catch(() => {});
}

function clearRead() {
  ElMessageBox.confirm('确认清空所有已读消息吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.post('/messages/batch/clear-read');
      ElMessage.success('已清空已读消息');
      loadList();
    })
    .catch(() => {});
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

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
}

.w-180 {
  width: 180px;
}

.w-140 {
  width: 140px;
}

.actions {
  margin-bottom: 0.5rem;
  display: flex;
  gap: 0.5rem;
}

.list {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.75rem 0.75rem 0.5rem;
}

.msg {
  padding: 0.5rem 0.5rem 0.35rem;
  border-bottom: 1px solid #f3f4f6;
}

.msg.unread {
  background: #eff6ff;
}

.msg:last-child {
  border-bottom: none;
}

.msg-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.tag {
  font-size: 0.75rem;
  padding: 2px 6px;
  border-radius: 999px;
  background: #e5e7eb;
  color: #374151;
}

.title {
  font-weight: 500;
}

.time {
  margin-left: auto;
  font-size: 0.8rem;
  color: #9ca3af;
}

.content {
  margin-top: 0.25rem;
  font-size: 0.9rem;
  color: #4b5563;
}

.msg-actions {
  margin-top: 0.25rem;
  display: flex;
  gap: 0.5rem;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.75rem;
}
</style>

