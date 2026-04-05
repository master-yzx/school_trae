<template>
  <AppLayout>
    <main class="main">
      <section class="section">
        <el-button text type="primary" @click="goBack">← 返回列表</el-button>
      </section>

      <section class="section detail">
        <div class="media">
          <el-image
            v-if="images.length"
            :src="images[activeImageIndex]"
            :preview-src-list="images"
            fit="cover"
          />
          <div v-else class="media-empty">
            暂无图片
          </div>

          <div v-if="images.length > 1" class="thumbs">
            <button
              v-for="(url, idx) in images"
              :key="url"
              :class="['thumb', { active: idx === activeImageIndex }]"
              @click="activeImageIndex = idx"
            >
              <img :src="url" alt="" />
            </button>
          </div>

          <video
            v-if="videoUrl"
            class="video"
            controls
            :src="videoUrl"
          ></video>
        </div>

        <div class="info">
          <h1 class="title">{{ detail?.title }}</h1>
          <div class="price-row">
            <span class="price">￥{{ detail?.price }}</span>
            <span class="tag" v-if="detail?.conditionText">{{ detail.conditionText }}</span>
            <span class="tag" v-if="detail?.deliveryTypeText">{{ detail.deliveryTypeText }}</span>
            <span class="tag" v-if="detail?.freeShipping">包邮</span>
          </div>

          <div class="meta">
            <span v-if="detail?.categoryName">分类：{{ detail.categoryName }}</span>
            <span v-if="detail?.campusName">校园：{{ detail.campusName }}</span>
            <span v-if="detail?.pickupLocation">自提地点：{{ detail.pickupLocation }}</span>
          </div>

          <div class="actions">
            <el-button type="primary" @click="handleBuy">立即购买</el-button>
            <el-button @click="handleAddCart">加入购物车</el-button>
            <el-button text @click="handleCollect">{{ collected ? '取消收藏' : '收藏' }}</el-button>
            <el-button text @click="handleConsult">在线咨询</el-button>
          </div>

          <div class="seller-card" v-if="detail">
            <h3>卖家信息</h3>
            <p>{{ detail.sellerName }} · {{ detail.sellerCampusName }}</p>
            <p v-if="detail.sellerProductCount != null">
              在售闲置：{{ detail.sellerProductCount }} 件
            </p>
            <el-button size="small" type="primary" plain @click="goSellerShop">
              查看该卖家全部闲置
            </el-button>
          </div>
        </div>
      </section>

      <section class="section">
        <SectionTitle title="图文详情" />
        <div class="desc" v-html="detail?.descriptionHtml"></div>
      </section>

      <section class="section">
        <SectionTitle title="相关推荐" />
        <div class="product-grid">
          <ProductCard
            v-for="item in relatedList"
            :key="item.id"
            :product="item"
            @click="goProduct(item.id)"
          />
        </div>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import ProductCard from '../../components/common/ProductCard.vue';
import request from '../../utils/request';
import { useAuthStore } from '../../stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const detail = ref(null);
const mediaList = ref([]);
const relatedList = ref([]);
const collected = ref(false);

const images = computed(() =>
  mediaList.value.filter((m) => m.type === 'IMAGE').map((m) => m.url),
);
const videoUrl = computed(() => {
  const video = mediaList.value.find((m) => m.type === 'VIDEO');
  return video ? video.url : '';
});

const activeImageIndex = ref(0);

function goBack() {
  router.back();
}

function goProduct(id) {
  router.push({ name: 'ProductDetail', params: { id } });
}

async function handleBuy() {
  if (!detail.value?.id) return;
  try {
    const res = await request.post('/orders/create', null, {
      params: { productId: detail.value.id, quantity: 1 },
    });
    const orderId = (res && res.data !== undefined) ? res.data : res;
    if (orderId) {
      ElMessage.success('订单已创建');
      router.push({ name: 'OrderDetail', params: { id: orderId }, query: { role: 'USER' } });
    } else {
      ElMessage.warning('请先登录或商品不存在');
    }
  } catch (e) {
    ElMessage.warning(e?.response?.data?.message || '创建订单失败，请先登录');
  }
}

function isLoggedIn() {
  return !!(authStore.accessToken || localStorage.getItem('accessToken'));
}

async function handleAddCart() {
  if (!detail.value?.id) return;
  if (!isLoggedIn()) {
    ElMessage.warning('请先登录后再加入购物车');
    return;
  }
  try {
    await request.post('/cart/add', null, {
      params: { productId: detail.value.id, quantity: 1 },
    });
    ElMessage.success('已加入购物车');
  } catch (e) {
    ElMessage.warning(e?.response?.data?.message || '请先登录后再加入购物车');
  }
}

async function handleCollect() {
  if (!detail.value?.id) return;
  if (!isLoggedIn()) {
    ElMessage.warning('请先登录');
    return;
  }
  try {
    if (collected.value) {
      await request.delete(`/favorite/product/${detail.value.id}`);
      collected.value = false;
      ElMessage.success('已取消收藏');
    } else {
      await request.post('/favorite/add', null, { params: { productId: detail.value.id } });
      collected.value = true;
      ElMessage.success('已收藏');
    }
  } catch (e) {
    ElMessage.warning(e?.response?.data?.message || '请先登录');
  }
}

async function handleConsult() {
  if (!detail.value?.id) return;
  if (!isLoggedIn()) {
    ElMessage.warning('请先登录后再咨询卖家');
    return;
  }
  try {
    const res = await request.post('/chat/start', { productId: detail.value.id });
    const sessionId = (res && res.data !== undefined) ? res.data : res;
    if (sessionId) {
      router.push({ name: 'ChatCenter', query: { sessionId } });
    } else {
      ElMessage.warning('无法发起聊天，请稍后重试');
    }
  } catch (e) {
    const msg = e?.response?.status === 401 || e?.response?.data?.code === 401
      ? '请先登录后再咨询卖家'
      : (e?.response?.data?.message || '请先登录后再咨询卖家');
    ElMessage.warning(msg);
  }
}

function goSellerShop() {
  if (!detail.value?.sellerId) return;
  router.push({ name: 'SellerShop', params: { id: detail.value.sellerId } });
}

async function loadData() {
  const id = route.params.id;
  const needCheckFavorite = isLoggedIn();
  const [detailRes, relatedRes, checkRes] = await Promise.all([
    request.get(`/products/${id}`),
    request.get(`/products/${id}/related`),
    needCheckFavorite
      ? request.get('/favorite/check', { params: { productId: id } }).catch(() => null)
      : Promise.resolve(null),
  ]);
  const detailData = (detailRes && detailRes.data !== undefined) ? detailRes.data : detailRes;
  detail.value = detailData || null;
  mediaList.value = (detail.value && detail.value.mediaList) || [];
  relatedList.value = (relatedRes && relatedRes.data !== undefined) ? relatedRes.data : (relatedRes || []);
  if (needCheckFavorite && checkRes && checkRes.data !== undefined) {
    collected.value = !!checkRes.data;
  } else {
    collected.value = false;
  }

  // 记录浏览历史（忽略失败）
  try {
    await request.post('/history/add', null, { params: { productId: id } });
  } catch (e) {
    // ignore
  }
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1.5rem;
}

.detail {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1.1fr);
  gap: 1.5rem;
}

.media {
  background: #ffffff;
  border-radius: 16px;
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.media :deep(.el-image) {
  width: 100%;
  height: 320px;
  border-radius: 12px;
  overflow: hidden;
}

.media-empty {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: #f3f4f6;
  color: #9ca3af;
}

.thumbs {
  display: flex;
  gap: 0.5rem;
}

.thumb {
  border: none;
  padding: 0;
  background: transparent;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  width: 60px;
  height: 60px;
  opacity: 0.7;
}

.thumb.active {
  outline: 2px solid #2563eb;
  opacity: 1;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video {
  margin-top: 0.25rem;
  width: 100%;
  border-radius: 12px;
  background: #000;
}

.info {
  background: #ffffff;
  border-radius: 16px;
  padding: 1rem 1.25rem;
}

.title {
  margin: 0 0 0.75rem;
  font-size: 1.3rem;
}

.price-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.price {
  color: #ef4444;
  font-weight: 700;
  font-size: 1.35rem;
}

.tag {
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
  font-size: 0.9rem;
  color: #4b5563;
  margin-bottom: 0.75rem;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.seller-card {
  padding: 0.75rem;
  border-radius: 12px;
  background: #f9fafb;
  font-size: 0.9rem;
  color: #4b5563;
}

.seller-card h3 {
  margin: 0 0 0.5rem;
  font-size: 1rem;
}

.desc {
  background: #ffffff;
  border-radius: 16px;
  padding: 1rem 1.25rem;
  font-size: 0.95rem;
  line-height: 1.7;
  color: #374151;
}

.desc :deep(p) {
  margin: 0 0 0.5rem;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1rem;
}
</style>

