<template>
  <el-carousel
    v-if="banners.length"
    height="220px"
    indicator-position="outside"
  >
    <el-carousel-item v-for="item in banners" :key="item.id">
      <div class="banner-item">
        <img :src="item.imageUrl" :alt="item.title" />
        <div class="banner-text">
          <h3>{{ item.title }}</h3>
          <p v-if="item.subtitle">{{ item.subtitle }}</p>
        </div>
      </div>
    </el-carousel-item>
  </el-carousel>
  <div v-else class="banner-empty">
    暂无 Banner，稍后由管理员在后台配置
  </div>
</template>

<script setup>
defineProps({
  banners: {
    type: Array,
    default: () => [],
  },
});
</script>

<style scoped>
.banner-item {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  border-radius: 16px;
}

.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-text {
  position: absolute;
  inset: auto 1.5rem 1.5rem;
  color: #f9fafb;
  text-shadow: 0 8px 20px rgba(15, 23, 42, 0.55);
}

.banner-text h3 {
  margin: 0 0 0.25rem;
  font-size: 1.3rem;
}

.banner-text p {
  margin: 0;
  font-size: 0.9rem;
}

.banner-empty {
  height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: linear-gradient(135deg, #eef2ff, #fdf2ff);
  color: #6b7280;
  font-size: 0.9rem;
}
</style>

