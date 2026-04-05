<template>
  <AppLayout>
    <main class="main">
      <section class="top">
        <div class="user-card" v-if="displayProfile">
          <el-avatar :size="64" :src="displayProfile.avatarUrl || undefined">
            {{ (displayProfile.nickname || '用户').charAt(0) }}
          </el-avatar>
          <div class="info">
            <div class="name-row">
              <span class="name">{{ displayProfile.nickname || '未设置昵称' }}</span>
              <span class="role-tag">{{ roleLabel }}</span>
            </div>
            <div class="meta">
              <span v-if="displayProfile.studentNo">学号：{{ maskedStudentNo }}</span>
              <span v-if="displayProfile.phoneMasked">手机：{{ displayProfile.phoneMasked }}</span>
              <span v-if="displayProfile.campusName">院校：{{ displayProfile.campusName }}</span>
            </div>
          </div>
        </div>
        <div class="user-card login-tip" v-else>
          <span>请先登录后查看个人信息</span>
          <el-button type="primary" size="small" @click="$router.push({ name: 'Auth' })">去登录</el-button>
        </div>

        <div class="message-card">
          <div class="message-header">
            <div>站内消息</div>
          </div>
          <div class="message-actions">
            <el-badge :value="messageUnreadCount" :max="99" :hidden="messageUnreadCount <= 0">
              <el-button type="primary" text @click="goMessages">进入消息中心</el-button>
            </el-badge>
            <el-badge :value="chatUnreadCount" :max="99" :hidden="chatUnreadCount <= 0">
              <el-button type="primary" text @click="goChat">进入聊天中心</el-button>
            </el-badge>
          </div>
        </div>
      </section>

      <section class="features">
        <SectionTitle title="常用功能" />
        <div class="grid">
          <button
            v-for="item in features"
            :key="item.code"
            class="feature-item"
            @click="goFeature(item.code)"
          >
            <span class="feature-name">{{ item.name }}</span>
          </button>
        </div>
      </section>

      <section class="dash">
        <div class="panel">
          <div class="panel-head">
            <div class="panel-title">最近浏览</div>
            <el-button text type="primary" @click="goFeature('history')">查看全部</el-button>
          </div>
          <div class="history-list" v-if="recentHistory.length">
            <button
              v-for="item in recentHistory"
              :key="item.id"
              class="history-item"
              @click="goProduct(item.productId)"
            >
              <img class="thumb" :src="item.coverUrl" :alt="item.title" />
              <div class="h-info">
                <div class="h-title">{{ item.title }}</div>
                <div class="h-meta">
                  <span class="price">￥{{ item.price }}</span>
                  <span v-if="item.campusName">· {{ item.campusName }}</span>
                </div>
              </div>
            </button>
          </div>
          <el-empty v-else description="暂无浏览记录" />
        </div>

        <div class="panel">
          <div class="panel-head">
            <div class="panel-title">为你推荐</div>
            <el-button text type="primary" @click="goFeature('products')">逛逛更多</el-button>
          </div>
          <div class="recommend-grid">
            <ProductCard v-for="p in recommendList" :key="p.id" :product="p" />
          </div>
          <el-empty v-if="!recommendList.length" description="暂无推荐商品" />
        </div>
      </section>

      <section class="section">
        <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
        <el-button class="ml8" @click="goHome">返回首页</el-button>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import ProductCard from '../../components/common/ProductCard.vue';
import request from '../../utils/request';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const profile = ref(null);
const features = ref([]);
const messageUnreadCount = ref(0);
const chatUnreadCount = ref(0);
const recentHistory = ref([]);
const recommendList = ref([]);

// 展示用的资料：接口返回的 profile 优先，未返回但已登录时用登录态信息兜底
const displayProfile = computed(() => {
  if (profile.value) return profile.value;
  const user = authStore.user;
  if (user && authStore.accessToken) {
    return {
      nickname: user.nickname || user.username || '用户',
      role: user.role || 'USER',
      avatarUrl: user.avatarUrl || '',
      studentNo: user.studentNo || '',
      phoneMasked: user.phoneMasked || '',
      campusName: user.campusName || '',
    };
  }
  return null;
});

const roleLabel = computed(() => {
  if (!displayProfile.value) return '';
  switch (displayProfile.value.role) {
    case 'SELLER':
      return '卖家';
    case 'ADMIN':
      return '管理员';
    default:
      return '用户';
  }
});

const maskedStudentNo = computed(() => {
  if (!displayProfile.value?.studentNo) return '';
  const v = displayProfile.value.studentNo;
  if (v.length <= 4) return v;
  return `${v.slice(0, 2)}****${v.slice(-2)}`;
});

function goFeature(code) {
  if (code === 'seller-products') {
    router.push({ name: 'SellerProductList' });
    return;
  }
  if (code === 'seller-orders') {
    router.push({ name: 'OrderList', query: { role: 'SELLER' } });
    return;
  }
  if (code === 'products') {
    router.push({ name: 'ProductList' });
    return;
  }
  if (code === 'settings') {
    router.push({ name: 'AccountSettings' });
    return;
  }
  if (code === 'favorite') {
    router.push({ name: 'FavoriteList' });
    return;
  }
  if (code === 'history') {
    router.push({ name: 'BrowseHistory' });
    return;
  }
  if (code === 'cart') {
    router.push({ name: 'CartList' });
    return;
  }
  if (code === 'order') {
    router.push({ name: 'OrderList' });
    return;
  }
  ElMessage.info(`功能「${code}」稍后接入对应页面`);
}

function handleLogout() {
  request.post('/auth/logout').finally(() => {
    authStore.clear();
    router.push('/');
  });
}

function goHome() {
  router.push('/');
}

function goMessages() {
  router.push({ name: 'MessageList' });
}

function goChat() {
  router.push({ name: 'ChatCenter' });
}

function goProduct(id) {
  if (!id) return;
  router.push({ name: 'ProductDetail', params: { id } });
}

async function loadData() {
  const safeGet = async (url, config) => {
    try {
      const res = await request.get(url, config);
      return res?.data;
    } catch (e) {
      return null;
    }
  };

  const [profileRes, featureRes, unreadRes, historyRes, recommendRes] = await Promise.all([
    safeGet('/user/profile'),
    safeGet('/user/features'),
    safeGet('/user/unread-counts'),
    safeGet('/history/list', { params: { pageNum: 1, pageSize: 6 } }),
    safeGet('/products/recommend'),
  ]);

  const profileData = (profileRes && profileRes.data !== undefined) ? profileRes.data : profileRes || null;
  // 若本次请求失败（profileData 为空），保留上一次成功的 profile，避免信息突然消失
  if (profileData) {
    profile.value = profileData;
  }
  features.value = (featureRes && featureRes.data !== undefined) ? featureRes.data : (featureRes || []);
  const unreadData = (unreadRes && unreadRes.data !== undefined) ? unreadRes.data : (unreadRes || null);
  messageUnreadCount.value = unreadData?.messageUnread ?? 0;
  chatUnreadCount.value = unreadData?.chatUnread ?? 0;
  const historyPage = (historyRes && historyRes.data !== undefined) ? historyRes.data : historyRes;
  recentHistory.value = (historyPage && historyPage.list) ? historyPage.list : [];
  recommendList.value = (recommendRes && recommendRes.data !== undefined) ? recommendRes.data : (recommendRes || []);

  // 同步登录态中的用户信息，保证导航栏头像/昵称与个人中心一致
  if (profileData && authStore.accessToken) {
    authStore.setAuth({
      accessToken: authStore.accessToken,
      user: {
        ...(authStore.user || {}),
        id: profileData.id ?? authStore.user?.id,
        nickname: profileData.nickname || authStore.user?.nickname,
        role: profileData.role || authStore.user?.role,
        avatarUrl: profileData.avatarUrl || authStore.user?.avatarUrl,
      },
    });
  }
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.main {
  max-width: 960px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.top {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(0, 1.1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.user-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 1rem 1.25rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-card.login-tip {
  justify-content: space-between;
  color: #6b7280;
}

.info {
  flex: 1;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
}

.name {
  font-size: 1.1rem;
  font-weight: 600;
}

.role-tag {
  font-size: 0.8rem;
  padding: 2px 8px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  font-size: 0.85rem;
  color: #4b5563;
}

.message-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 1rem 1.25rem;
}

.message-header > div:first-child {
  font-weight: 600;
  margin-bottom: 0.35rem;
}

.unread {
  font-size: 0.9rem;
}

.message-actions {
  margin-top: 0.5rem;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 14px;
}

.message-actions :deep(.el-badge) {
  display: inline-flex;
}

.message-actions :deep(.el-badge__content) {
  transform: translate(120%, -40%);
}

.message-actions .el-button {
  padding: 0;
}

.message-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 0.9rem 1rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.5rem;
}

.unread {
  font-size: 0.9rem;
}

.features {
  margin-bottom: 1.5rem;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 0.75rem;
}

.feature-item {
  border: none;
  border-radius: 12px;
  background: #ffffff;
  padding: 0.9rem 1rem;
  text-align: left;
  cursor: pointer;
  font-size: 0.95rem;
}

.feature-item:hover {
  background: #eff6ff;
}

.dash {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1fr);
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.panel {
  background: #ffffff;
  border-radius: 16px;
  padding: 0.9rem 1rem 1rem;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.panel-title {
  font-size: 1rem;
  font-weight: 700;
  color: #111827;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.history-item {
  border: 1px solid #eef2f7;
  background: #f9fafb;
  border-radius: 12px;
  padding: 0.5rem;
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr);
  gap: 0.75rem;
  text-align: left;
  cursor: pointer;
}

.history-item:hover {
  background: #ffffff;
  border-color: #dbeafe;
}

.thumb {
  width: 72px;
  height: 54px;
  border-radius: 10px;
  object-fit: cover;
}

.h-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #111827;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.h-meta {
  margin-top: 0.25rem;
  font-size: 0.85rem;
  color: #6b7280;
  display: flex;
  gap: 0.35rem;
  align-items: center;
}

.h-meta .price {
  color: #ef4444;
  font-weight: 700;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}

.section {
  margin-top: 0.5rem;
}

.ml8 {
  margin-left: 8px;
}

@media (max-width: 920px) {
  .top {
    grid-template-columns: 1fr;
  }
  .dash {
    grid-template-columns: 1fr;
  }
  .recommend-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  }
}
</style>

