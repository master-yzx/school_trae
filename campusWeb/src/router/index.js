import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import HomeIndex from '../views/home/HomeIndex.vue';
import ProductList from '../views/product/ProductList.vue';
import ProductDetail from '../views/product/ProductDetail.vue';
import AuthIndex from '../views/auth/AuthIndex.vue';
import ForgotPassword from '../views/auth/ForgotPassword.vue';
import ProfileHome from '../views/profile/ProfileHome.vue';
import AccountSettings from '../views/profile/AccountSettings.vue';
import FavoriteList from '../views/favorite/FavoriteList.vue';
import BrowseHistory from '../views/history/BrowseHistory.vue';
import CartList from '../views/cart/CartList.vue';
import SellerProductList from '../views/product/SellerProductList.vue';
import ProductPublish from '../views/product/ProductPublish.vue';
import OrderList from '../views/order/OrderList.vue';
import OrderDetail from '../views/order/OrderDetail.vue';
import MessageList from '../views/message/MessageList.vue';
import ChatCenter from '../views/chat/ChatCenter.vue';
import SellerShop from '../views/seller/SellerShop.vue';
import TradeRules from '../views/system/TradeRules.vue';
import PrivacyPolicy from '../views/system/PrivacyPolicy.vue';
import NoticeCenter from '../views/system/NoticeCenter.vue';
import ForumList from '../views/forum/ForumList.vue';
import ForumDetail from '../views/forum/ForumDetail.vue';
import NotFound from '../views/notfound/NotFound.vue';
import AdminLayout from '../admin/layout/AdminLayout.vue';
import AdminDashboard from '../admin/dashboard/AdminDashboard.vue';
import AdminProductReview from '../admin/review/AdminProductReview.vue';
import AdminCategory from '../admin/category/AdminCategory.vue';
import AdminProductContent from '../admin/content/AdminProductContent.vue';
import AdminUserList from '../admin/user/AdminUserList.vue';
import AdminSellerList from '../admin/seller/AdminSellerList.vue';
import AdminOrderManage from '../admin/order/AdminOrderManage.vue';
import AdminStats from '../admin/stats/AdminStats.vue';
import AdminSystemSettings from '../admin/settings/AdminSystemSettings.vue';
import AdminManager from '../admin/manager/AdminManager.vue';
import AdminOperationLog from '../admin/log/AdminOperationLog.vue';
import AdminNoticeManage from '../admin/notice/AdminNoticeManage.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeIndex,
  },
  {
    path: '/products',
    name: 'ProductList',
    component: ProductList,
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: ProductDetail,
  },
  {
    path: '/auth',
    name: 'Auth',
    component: AuthIndex,
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword,
  },
  {
    path: '/profile',
    name: 'ProfileHome',
    component: ProfileHome,
    meta: { requiresAuth: true },
  },
  {
    path: '/settings',
    name: 'AccountSettings',
    component: AccountSettings,
    meta: { requiresAuth: true },
  },
  {
    path: '/favorites',
    name: 'FavoriteList',
    component: FavoriteList,
    meta: { requiresAuth: true },
  },
  {
    path: '/history',
    name: 'BrowseHistory',
    component: BrowseHistory,
    meta: { requiresAuth: true },
  },
  {
    path: '/cart',
    name: 'CartList',
    component: CartList,
    meta: { requiresAuth: true },
  },
  {
    path: '/seller/products',
    name: 'SellerProductList',
    component: SellerProductList,
    meta: { requiresAuth: true, requiresSeller: true },
  },
  {
    path: '/seller/products/publish',
    name: 'ProductPublish',
    component: ProductPublish,
    meta: { requiresAuth: true, requiresSeller: true },
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: OrderList,
    meta: { requiresAuth: true },
  },
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: OrderDetail,
    meta: { requiresAuth: true },
  },
  {
    path: '/messages',
    name: 'MessageList',
    component: MessageList,
    meta: { requiresAuth: true },
  },
  {
    path: '/chat',
    name: 'ChatCenter',
    component: ChatCenter,
    meta: { requiresAuth: true },
  },
  {
    path: '/sellers/:id',
    name: 'SellerShop',
    component: SellerShop,
    meta: { requiresAuth: true },
  },
  {
    path: '/rules',
    name: 'TradeRules',
    component: TradeRules,
  },
  {
    path: '/privacy',
    name: 'PrivacyPolicy',
    component: PrivacyPolicy,
  },
  {
    path: '/notice',
    name: 'NoticeCenter',
    component: NoticeCenter,
  },
  {
    path: '/forum',
    name: 'ForumList',
    component: ForumList,
  },
  {
    path: '/forum/:id',
    name: 'ForumDetail',
    component: ForumDetail,
  },
  {
    path: '/userInfo',
    name: 'UserInfo',
    component: () => import('../components/UserInfo.vue')
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
      },
      {
        path: 'review/products',
        name: 'AdminProductReview',
        component: AdminProductReview,
      },
      {
        path: 'categories',
        name: 'AdminCategory',
        component: AdminCategory,
      },
      {
        path: 'content',
        name: 'AdminProductContent',
        component: AdminProductContent,
      },
      {
        path: 'users',
        name: 'AdminUserList',
        component: AdminUserList,
      },
      {
        path: 'sellers',
        name: 'AdminSellerList',
        component: AdminSellerList,
      },
      {
        path: 'orders',
        name: 'AdminOrderManage',
        component: AdminOrderManage,
      },
      {
        path: 'stats',
        name: 'AdminStats',
        component: AdminStats,
      },
      {
        path: 'settings',
        name: 'AdminSystemSettings',
        component: AdminSystemSettings,
      },
      {
        path: 'notices',
        name: 'AdminNoticeManage',
        component: AdminNoticeManage,
      },
      {
        path: 'managers',
        name: 'AdminManager',
        component: AdminManager,
      },
      {
        path: 'logs',
        name: 'AdminOperationLog',
        component: AdminOperationLog,
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 };
  },
});

router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  const isLogin = !!auth.accessToken;
  const role = auth.user?.role;

  if (to.meta?.requiresAuth && !isLogin) {
    return next({ name: 'Auth', query: { tab: 'login', redirect: to.fullPath } });
  }

  if (to.meta?.requiresSeller && role !== 'SELLER') {
    return next({ name: 'ProfileHome' });
  }

  if (to.meta?.requiresAdmin && role !== 'ADMIN') {
    return next({ name: 'Home' });
  }

  return next();
});

export default router;

