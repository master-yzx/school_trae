<template>
  <article class="card" @click="goDetail">
    <div class="thumb-wrap">
      <img :src="product.coverUrl" :alt="product.title" />
      <span v-if="product.campusName" class="campus-tag">
        {{ product.campusName }}
      </span>
    </div>
    <h3 class="title" :title="product.title">
      {{ product.title }}
    </h3>
    <div class="meta">
      <span class="price">￥{{ product.price }}</span>
      <span class="extra">{{ product.sellerName }}</span>
    </div>
  </article>
</template>

<script setup>
import { useRouter } from 'vue-router';

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

const router = useRouter();

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
  border-radius: 14px;
  padding: 0.5rem 0.5rem 0.75rem;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  cursor: pointer;
  transition: transform 0.12s ease, box-shadow 0.12s ease;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.12);
}

.thumb-wrap {
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  height: 160px;
}

.thumb-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.campus-tag {
  position: absolute;
  left: 8px;
  bottom: 8px;
  background: rgba(15, 23, 42, 0.75);
  color: #f9fafb;
  padding: 2px 6px;
  border-radius: 999px;
  font-size: 11px;
}

.title {
  margin: 0.25rem 0 0;
  font-size: 0.95rem;
  line-height: 1.3;
  max-height: 2.6em;
  overflow: hidden;
}

.meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.8rem;
}

.price {
  color: #ef4444;
  font-weight: 600;
}

.extra {
  color: #6b7280;
}
</style>

