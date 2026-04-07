<template>
  <header class="header">
    <div class="header-inner">
      <div class="left-part">
        <div class="logo" @click="goHome">校园二手交易平台</div>
        <el-menu
          mode="horizontal"
          class="category-menu"
          :ellipsis="false"
          @select="handleMenuSelect"
        >
          <el-menu-item index="home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="products">
            <el-icon><Goods /></el-icon>
            <span>全部商品</span>
          </el-menu-item>
          <el-sub-menu index="category">
            <template #title>
              <el-icon><Grid /></el-icon>
              <span>全部分类</span>
            </template>
            <CategoryMenuNode
              v-for="item in categories"
              :key="item.id"
              :node="item"
            />
          </el-sub-menu>
          <el-menu-item index="rules">
            <el-icon><Document /></el-icon>
            <span>交易须知</span>
          </el-menu-item>
          <el-menu-item index="forum">
            <el-icon><ChatLineSquare /></el-icon>
            <span>论坛</span>
          </el-menu-item>
          <el-menu-item index="notice">
            <el-icon><Bell /></el-icon>
            <span>公告</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="search-part">
        <el-input
          v-model="keyword"
          placeholder="搜索教材 / 数码 / 家电等校园闲置"
          size="default"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <div class="right-part">
        <template v-if="!isLogin">
          <el-button type="primary" link @click="goLogin">登录</el-button>
          <el-button type="primary" @click="goRegister">注册</el-button>
        </template>
        <template v-else>
          <el-dropdown trigger="click" @command="onUserCommand">
            <div class="user-entry">
              <el-avatar :size="32" class="user-avatar" :src="avatarUrl || undefined">
                {{ (nickname || 'U').slice(0, 1) }}
              </el-avatar>
              <div class="user-meta">
                <div class="user-name">{{ nickname }}</div>
                <div v-if="roleLabel" class="user-role">{{ roleLabel }}</div>
              </div>
              <el-icon class="user-caret"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><UserFilled /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="orders">
                  <el-icon><List /></el-icon>
                  我的订单
                </el-dropdown-item>
                <el-dropdown-item v-if="role === 'SELLER'" command="sellerOrders">
                  <el-icon><List /></el-icon>
                  卖家订单
                </el-dropdown-item>
                <el-dropdown-item command="cart">
                  <el-icon><ShoppingCart /></el-icon>
                  我的购物车
                </el-dropdown-item>
                <el-dropdown-item command="messages">
                  <el-icon><Message /></el-icon>
                  消息中心
                </el-dropdown-item>
                <el-dropdown-item v-if="role === 'ADMIN'" command="admin">
                  <el-icon><List /></el-icon>
                  后台管理
                </el-dropdown-item>
                <el-dropdown-item v-if="role === 'SELLER'" command="publish">
                  <el-icon><UploadFilled /></el-icon>
                  我要发布
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>

    <div v-if="hotSearches && hotSearches.length" class="header-sub">
      <div class="hot-search">
        <span class="label">热门：</span>
        <button
          v-for="item in hotSearches"
          :key="item"
          class="hot-btn"
          @click="handleHotClick(item)"
        >
          {{ item }}
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';
import { useAuthStore } from '../../stores/auth';
import { ArrowDown, UserFilled, List, Message, UploadFilled, SwitchButton, ShoppingCart, House, Goods, Grid, Document, ChatLineSquare, Bell } from '@element-plus/icons-vue';
import CategoryMenuNode from './CategoryMenuNode.vue';

defineProps({
  categories: {
    type: Array,
    default: () => [],
  },
  hotSearches: {
    type: Array,
    default: () => [],
  },
  campuses: {
    type: Array,
    default: () => [],
  },
});

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const isLogin = computed(() => !!authStore.accessToken);
const nickname = computed(() => authStore.user?.nickname || '用户');
const role = computed(() => authStore.user?.role || 'USER');
const avatarUrl = computed(() => authStore.user?.avatarUrl || '');

const roleLabel = computed(() => {
  if (role.value === 'SELLER') return '卖家';
  if (role.value === 'ADMIN') return '管理员';
  return '';
});

const keyword = ref('');
const selectedCampusId = ref(null);

function syncFromRoute() {
  const q = route.query || {};
  if (typeof q.keyword === 'string') keyword.value = q.keyword;
  if (q.campusId != null) {
    const v = Number(q.campusId);
    selectedCampusId.value = Number.isFinite(v) ? v : null;
  } else {
    selectedCampusId.value = null;
  }
}

function handleSearch() {
  if (!keyword.value) return;
  router.push({
    name: 'ProductList',
    query: {
      keyword: keyword.value,
      campusId: selectedCampusId.value || undefined,
    },
  });
}

function handleHotClick(word) {
  keyword.value = word;
  handleSearch();
}

function handleMenuSelect(index) {
  if (!index || typeof index !== 'string') return;
  if (index === 'home') {
    router.push('/');
    return;
  }
  if (index === 'products') {
    router.push({ name: 'ProductList' });
    return;
  }
  if (index === 'category') {
    router.push({ name: 'ProductList' });
    return;
  }
  if (index === 'publish') {
    router.push({ name: 'ProductPublish' });
    return;
  }
  if (index === 'profile') {
    router.push({ name: 'ProfileHome' });
    return;
  }
  if (index === 'orders') {
    router.push({ name: 'OrderList' });
    return;
  }
  if (index === 'messages') {
    router.push({ name: 'MessageList' });
    return;
  }
  if (index === 'rules') {
    router.push({ name: 'TradeRules' });
    return;
  }
  if (index === 'notice') {
    router.push({ name: 'NoticeCenter' });
    return;
  }
  if (index === 'forum') {
    router.push({ name: 'ForumList' });
    return;
  }
  if (!index.startsWith('cat-')) return;

  const maybeCategoryId = Number(index.slice('cat-'.length));
  if (!Number.isFinite(maybeCategoryId) || maybeCategoryId <= 0) return;

  router.push({
    name: 'ProductList',
    query: {
      categoryId: maybeCategoryId,
      campusId: selectedCampusId.value || undefined,
    },
  });
}

function handleCampusChange() {
  if (route.name === 'ProductList') {
    router.replace({
      name: 'ProductList',
      query: {
        ...route.query,
        campusId: selectedCampusId.value || undefined,
      },
    });
  }
}

function goHome() {
  router.push('/');
}

function goLogin() {
  router.push({ name: 'Auth', query: { tab: 'login' } });
}

function goRegister() {
  router.push({ name: 'Auth', query: { tab: 'register' } });
}

function onUserCommand(cmd) {
  if (cmd === 'profile') return router.push({ name: 'ProfileHome' });
  if (cmd === 'orders') return router.push({ name: 'OrderList' });
  if (cmd === 'sellerOrders') return router.push({ name: 'OrderList', query: { role: 'SELLER' } });
  if (cmd === 'cart') return router.push({ name: 'CartList' });
  if (cmd === 'messages') return router.push({ name: 'MessageList' });
  if (cmd === 'publish') return router.push({ name: 'ProductPublish' });
  if (cmd === 'admin') return router.push({ name: 'AdminDashboard' });
  if (cmd === 'logout') return handleLogout();
}

function handleLogout() {
  request
    .post('/auth/logout')
    .finally(() => {
      authStore.clear();
      ElMessage.success('已退出登录');
      router.push('/');
    });
}

onMounted(() => {
  syncFromRoute();
});

watch(
  () => route.query,
  () => {
    syncFromRoute();
  },
);
</script>

<style scoped>
.header {
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.5rem 1rem;
  display: grid;
  grid-template-columns: auto minmax(0, 1.8fr) auto;
  gap: 0.75rem;
  align-items: center;
}

.left-part {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.logo {
  font-weight: 700;
  font-size: 1.25rem;
  color: #2563eb;
  cursor: pointer;
}

.category-menu {
  border-bottom: none;
}

.category-menu :deep(.el-menu-item),
.category-menu :deep(.el-sub-menu__title) {
  margin: 0 2px;
  padding: 0 12px;
}

.category-menu :deep(.el-menu-item) {
  display: flex;
  align-items: center;
  gap: 6px;
}

.search-part {
  display: flex;
  justify-content: center;
}

.search-input {
  max-width: 520px;
}

.search-input :deep(.el-input-group__append) {
  background-color: #2563eb;
  border-color: #2563eb;
}

.hot-search {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 12px;
  color: #6b7280;
}

.header-sub {
  border-top: 1px solid #f3f4f6;
  background: #ffffff;
}

.header-sub .hot-search {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.35rem 1rem 0.55rem;
  overflow: auto hidden;
  white-space: nowrap;
}

.hot-btn {
  border: none;
  background: transparent;
  color: #4b5563;
  padding: 0 4px;
  cursor: pointer;
}

.hot-btn:hover {
  color: #2563eb;
}

.right-part {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  align-items: center;
}

.user-entry {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 999px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  transition: box-shadow 0.15s ease, background 0.15s ease, border-color 0.15s ease;
}

.user-entry:hover {
  background: #ffffff;
  border-color: #d1d5db;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.12);
}

.user-avatar {
  box-shadow: 0 6px 14px rgba(37, 99, 235, 0.18);
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
  min-width: 0;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: #111827;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-role {
  margin-top: 2px;
  font-size: 12px;
  color: #6b7280;
}

.user-caret {
  color: #6b7280;
}

:deep(.el-dropdown-menu) {
  min-width: 180px;
  padding: 6px 2px;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  font-size: 14px;
}
</style>

