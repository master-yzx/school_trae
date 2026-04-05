<template>
  <div class="admin-layout">
    <header class="admin-header">
      <div class="brand">
        <div class="logo">校园二手交易平台</div>
        <div class="subtitle">管理控制台</div>
      </div>
      <div class="right">
        <el-button size="small" @click="$router.push('/')">返回前台首页</el-button>
      </div>
    </header>
    <div class="admin-body">
      <aside class="admin-sider">
        <el-menu
          class="menu"
          :default-active="$route.path"
          router
        >
          <el-menu-item index="/admin/dashboard">控制台</el-menu-item>
          <el-menu-item
            v-if="hasPermission('review')"
            index="/admin/review/products"
          >商品审核</el-menu-item>
          <el-menu-item
            v-if="hasPermission('category')"
            index="/admin/categories"
          >分类管理</el-menu-item>
          <el-menu-item
            v-if="hasPermission('content')"
            index="/admin/content"
          >内容管控</el-menu-item>
          <el-menu-item
            v-if="hasPermission('user')"
            index="/admin/users"
          >用户管理</el-menu-item>
          <el-menu-item
            v-if="hasPermission('seller')"
            index="/admin/sellers"
          >卖家管理</el-menu-item>
          <el-menu-item
            v-if="hasPermission('order')"
            index="/admin/orders"
          >订单管理</el-menu-item>
          <el-menu-item
            v-if="hasPermission('dashboard') || hasPermission('stats')"
            index="/admin/stats"
          >数据统计</el-menu-item>
          <el-menu-item
            v-if="hasPermission('settings')"
            index="/admin/settings"
          >系统配置</el-menu-item>
          <el-menu-item
            v-if="hasPermission('content')"
            index="/admin/notices"
          >公告管理</el-menu-item>
          <el-menu-item
            v-if="hasPermission('manager')"
            index="/admin/managers"
          >管理员管理</el-menu-item>
          <el-menu-item
            v-if="hasPermission('log')"
            index="/admin/logs"
          >操作日志</el-menu-item>
        </el-menu>
      </aside>
      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../utils/request';

const permissions = ref([]);

const hasPermission = (code) => {
  if (!code) return true;
  if (!permissions.value || permissions.value.length === 0) {
    // 未配置任何权限时，默认全部可见，方便初始超管
    return true;
  }
  return permissions.value.includes(code);
};

onMounted(async () => {
  try {
    const res = await request.get('/admin/managers/me/permissions');
    if (res.code === 0 && Array.isArray(res.data)) {
      permissions.value = res.data;
    }
  } catch (e) {
    // 出错时，保持默认（全部可见），避免把后台锁死
  }
});
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.admin-header {
  height: 56px;
  padding: 0 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #020617;
  color: #e5e7eb;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.6);
}

.brand {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.logo {
  font-size: 18px;
  font-weight: 700;
}

.subtitle {
  font-size: 12px;
  color: #9ca3af;
}

.admin-body {
  flex: 1;
  display: flex;
  min-height: 0;
}

.admin-sider {
  width: 220px;
  background: #0f172a;
  color: #e5e7eb;
}

.menu {
  border-right: none;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #9ca3af;
  --el-menu-active-color: #ffffff;
}

.admin-content {
  flex: 1;
  background: #f3f4f6;
  padding: 1rem 1.25rem 2rem;
  overflow: auto;
}
</style>

