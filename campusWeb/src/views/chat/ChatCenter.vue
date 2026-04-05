<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="聊天中心" />

      <section class="section chat-layout">
        <aside class="session-list">
          <div class="session-item" v-for="s in sessions" :key="s.id" :class="{ active: s.id === currentSessionId }" @click="openSession(s.id)">
            <div class="title-row">
              <span class="name">{{ s.otherNickname || '对方' }}</span>
              <span class="time">{{ s.lastTime }}</span>
            </div>
            <div class="meta-row">
              <span class="product" v-if="s.productTitle">{{ s.productTitle }}</span>
              <el-badge v-if="s.unreadCount" :value="s.unreadCount" :max="99" class="badge" />
            </div>
            <div class="last">{{ s.lastMessage }}</div>
          </div>
          <div v-if="!sessions.length" class="empty-tip">暂无会话，先去商品页或订单中点击「联系对方」试试。</div>
        </aside>

        <section class="chat-panel" v-if="currentSession">
          <header class="chat-header">
            <div class="left">
              <div class="name">{{ currentSession.session.otherNickname || '对方' }}</div>
              <div class="sub" v-if="currentSession.session.productTitle">
                {{ currentSession.session.productTitle }}
              </div>
            </div>
          </header>

          <div class="chat-body" ref="messageBox">
            <div
              v-for="m in currentSession.messages"
              :key="m.id"
              class="msg-row"
              :class="{ mine: m.fromUserId === currentUserId }"
            >
              <div class="bubble">
                <div class="content">{{ m.content }}</div>
                <div class="time">{{ m.createdAt }}</div>
              </div>
            </div>
          </div>

          <footer class="chat-input">
            <el-input
              v-model="draft"
              type="textarea"
              :rows="3"
              maxlength="500"
              show-word-limit
              placeholder="输入要发送的内容，回车发送，Shift+回车换行"
              @keyup.enter.exact.prevent="handleSend"
            />
            <div class="actions">
              <el-button type="primary" @click="handleSend">发送</el-button>
            </div>
          </footer>
        </section>

        <section class="chat-panel empty" v-else>
          <div class="empty-tip">请选择左侧会话，或从商品详情/订单详情点击「联系对方」开启聊天。</div>
        </section>
      </section>

      <section class="section">
        <el-button @click="goBack">返回个人中心</el-button>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';
import { useAuthStore } from '../../stores/auth';
import { connectChat, disconnectChat, onChatMessage, sendChatMessage } from '../../utils/chatClient';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const currentUserId = computed(() => authStore.user?.id || null);

const sessions = ref([]);
const currentSessionId = ref(null);
const currentSession = ref(null);
const draft = ref('');
const messageBox = ref();

let unsubscribe = null;

async function loadSessions() {
  const res = await request.get('/chat/sessions', { params: { pageNum: 1, pageSize: 50 } });
  const page = res.data || {};
  sessions.value = page.list || [];
}

async function loadSessionDetail(id) {
  if (!id) return;
  const res = await request.get(`/chat/sessions/${id}`);
  currentSession.value = res.data || null;
  await nextTick();
  scrollToBottom();
}

function scrollToBottom() {
  if (!messageBox.value) return;
  const el = messageBox.value;
  el.scrollTop = el.scrollHeight;
}

async function openSession(id) {
  currentSessionId.value = id;
  const s = sessions.value.find((it) => it.id === id);
  if (s) {
    s.unreadCount = 0;
  }
  await loadSessionDetail(id);
}

function handleIncoming(msg) {
  // 更新会话列表 lastMessage/lastTime/unread
  const s = sessions.value.find((it) => it.id === msg.sessionId);
  if (s) {
    s.lastMessage = msg.content;
    s.lastTime = msg.createdAt;
    if (msg.fromUserId !== currentUserId.value) {
      s.unreadCount = (s.unreadCount || 0) + 1;
    }
  }

  if (currentSessionId.value === msg.sessionId && currentSession.value) {
    currentSession.value.messages.push(msg);
    if (msg.fromUserId !== currentUserId.value) {
      const sess = sessions.value.find((it) => it.id === msg.sessionId);
      if (sess) sess.unreadCount = 0;
    }
    nextTick(() => scrollToBottom());
  } else {
    // 新会话或当前未打开，刷新列表保证看到最新排序
    loadSessions().catch(() => {});
  }
}

function handleSend() {
  if (!currentSessionId.value) {
    ElMessage.warning('请先选择一个会话');
    return;
  }
  const text = (draft.value || '').trim();
  if (!text) {
    return;
  }
  sendChatMessage(currentSessionId.value, text);
  draft.value = '';
}

function goBack() {
  router.push({ name: 'ProfileHome' });
}

onMounted(async () => {
  await loadSessions();

  const qSessionId = route.query.sessionId;
  if (qSessionId) {
    const sid = Number(qSessionId);
    if (Number.isFinite(sid)) {
      currentSessionId.value = sid;
      await loadSessionDetail(sid);
    }
  }

  connectChat();
  unsubscribe = onChatMessage(handleIncoming);
});

onBeforeUnmount(() => {
  if (unsubscribe) unsubscribe();
  // 不主动断开，全局复用连接；如果希望离开即断开，可打开下面一行：
  // disconnectChat();
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

.chat-layout {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 0.75rem;
}

.session-list {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.5rem;
  max-height: 520px;
  overflow-y: auto;
}

.session-item {
  padding: 0.4rem 0.5rem;
  border-radius: 8px;
  cursor: pointer;
}

.session-item + .session-item {
  margin-top: 0.25rem;
}

.session-item.active {
  background: #eff6ff;
}

.title-row {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
}

.title-row .name {
  font-weight: 500;
}

.title-row .time {
  margin-left: auto;
  font-size: 0.75rem;
  color: #9ca3af;
}

.meta-row {
  display: flex;
  align-items: center;
  margin-top: 2px;
  font-size: 0.8rem;
  color: #6b7280;
}

.meta-row .product {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta-row .badge {
  margin-left: 4px;
}

.last {
  margin-top: 2px;
  font-size: 0.8rem;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-panel {
  background: #ffffff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  min-height: 360px;
}

.chat-panel.empty {
  align-items: center;
  justify-content: center;
}

.chat-header {
  padding: 0.6rem 0.9rem;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
}

.chat-header .name {
  font-weight: 500;
}

.chat-header .sub {
  font-size: 0.8rem;
  color: #6b7280;
}

.chat-body {
  flex: 1;
  padding: 0.75rem 0.9rem;
  overflow-y: auto;
}

.msg-row {
  display: flex;
  margin-bottom: 0.35rem;
}

.msg-row.mine {
  justify-content: flex-end;
}

.bubble {
  max-width: 70%;
  background: #f3f4f6;
  border-radius: 12px;
  padding: 0.35rem 0.6rem;
  font-size: 0.9rem;
}

.msg-row.mine .bubble {
  background: #2563eb;
  color: #ffffff;
}

.bubble .time {
  margin-top: 2px;
  font-size: 0.75rem;
  opacity: 0.8;
}

.chat-input {
  border-top: 1px solid #e5e7eb;
  padding: 0.5rem 0.75rem;
}

.chat-input .actions {
  margin-top: 0.35rem;
  text-align: right;
}

.empty-tip {
  font-size: 0.9rem;
  color: #6b7280;
  padding: 0.75rem;
}

@media (max-width: 768px) {
  .chat-layout {
    grid-template-columns: 1fr;
  }
}
</style>

