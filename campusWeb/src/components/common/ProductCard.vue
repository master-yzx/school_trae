<template>
  <article class="card" @click="goDetail">
    <div class="thumb-wrap">
      <div class="img-container">
        <img 
          :src="product.coverUrl" 
          :alt="product.title"
          @load="imageLoaded = true"
        />
        <div v-if="!imageLoaded" class="image-placeholder">
          <el-icon class="loading-icon"><Loading /></el-icon>
        </div>
      </div>
      <span v-if="product.campusName" class="campus-tag">
        {{ product.campusName }}
      </span>
      <span v-if="product.status === 'HOT'" class="hot-tag">
        热门
      </span>
    </div>
    <h3 class="title" :title="product.title">
      {{ product.title }}
    </h3>
    <div class="meta">
      <span class="price">￥{{ product.price }}</span>
      <span class="extra">{{ product.sellerName }}</span>
    </div>
    <div class="stats">
      <span class="stat-item">
        <el-icon class="stat-icon"><View /></el-icon>
        {{ product.viewCount || 0 }}
      </span>
      <span class="stat-item">
        <el-icon class="stat-icon"><Star /></el-icon>
        {{ product.favoriteCount || 0 }}
      </span>
    </div>
  </article>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Loading, View, Star } from '@element-plus/icons-vue';

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

const router = useRouter();
const imageLoaded = ref(false);

function goDetail() {
  if (!props.product) return;
  const id = props.product.productId ?? props.product.id;
  if (!id) return;
  router.push({ name: 'ProductDetail', params: { id } });
}
</script>

<style scoped>
.card {
  background: #ffffff;
  border-radius: 16px;
  padding: 1rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.6s ease;
}

.card:hover::before {
  left: 100%;
}

.card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.thumb-wrap {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  height: 180px;
  margin: -1rem -1rem 0.75rem;
}

.img-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.img-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.card:hover .img-container img {
  transform: scale(1.1);
}

.image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-icon {
  font-size: 2rem;
  color: #2563eb;
  animation: spin 1.5s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.campus-tag {
  position: absolute;
  left: 12px;
  bottom: 12px;
  background: rgba(37, 99, 235, 0.9);
  color: #ffffff;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
}

.hot-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #ef4444, #f97316);
  color: #ffffff;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

.title {
  margin: 0;
  font-size: 1rem;
  line-height: 1.4;
  max-height: 2.8em;
  overflow: hidden;
  font-weight: 600;
  color: #111827;
  transition: color 0.3s ease;
}

.card:hover .title {
  color: #2563eb;
}

.meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.875rem;
  margin-top: auto;
}

.price {
  color: #ef4444;
  font-weight: 700;
  font-size: 1.125rem;
  transition: color 0.3s ease;
}

.card:hover .price {
  color: #dc2626;
  transform: scale(1.05);
  display: inline-block;
}

.extra {
  color: #6b7280;
  font-size: 0.8rem;
  transition: color 0.3s ease;
}

.card:hover .extra {
  color: #4b5563;
}

.stats {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: #9ca3af;
  margin-top: 0.25rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  transition: color 0.3s ease;
}

.card:hover .stat-item {
  color: #6b7280;
}

.stat-icon {
  font-size: 12px;
}

@media (max-width: 768px) {
  .card {
    padding: 0.75rem;
  }
  
  .thumb-wrap {
    height: 150px;
    margin: -0.75rem -0.75rem 0.5rem;
  }
  
  .title {
    font-size: 0.9rem;
  }
  
  .price {
    font-size: 1rem;
  }
}
</style>

