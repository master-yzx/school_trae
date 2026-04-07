<template>
  <div class="banner-container">
    <el-carousel
      v-if="banners.length"
      height="320px"
      indicator-position="outside"
      :interval="5000"
      type="card"
      :autoplay="true"
      :pause-on-hover="true"
    >
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-item">
          <div class="img-container">
            <img 
              :src="item.imageUrl" 
              :alt="item.title"
              @load="handleImageLoad"
              @error="handleImageError"
            />
            <div v-if="loading" class="image-placeholder">
              <el-icon class="loading-icon"><Loading /></el-icon>
            </div>
            <div class="banner-overlay"></div>
          </div>
          <div class="banner-text">
            <h3 class="banner-title">{{ item.title }}</h3>
            <p v-if="item.subtitle" class="banner-subtitle">{{ item.subtitle }}</p>
            <el-button 
              v-if="item.link" 
              type="primary" 
              :href="item.link" 
              target="_blank"
              class="banner-button"
            >
              查看详情
            </el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    <div v-else class="banner-empty">
      <div class="empty-content">
        <el-icon class="empty-icon"><Picture /></el-icon>
        <h4>暂无 Banner</h4>
        <p>稍后由管理员在后台配置</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Loading, Picture } from '@element-plus/icons-vue';

defineProps({
  banners: {
    type: Array,
    default: () => [],
  },
});

const loading = ref(true);

function handleImageLoad() {
  loading.value = false;
}

function handleImageError(event) {
  loading.value = false;
  // 设置默认图片
  event.target.src = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=campus%20second%20market%20banner%20with%20modern%20design&image_size=landscape_16_9';
}
</script>

<style scoped>
.banner-container {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 320px;
  overflow: hidden;
  border-radius: 20px;
  transition: transform 0.5s ease;
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
  transition: transform 7s ease;
}

.banner-item:hover .img-container img {
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
  z-index: 1;
}

.loading-icon {
  font-size: 3rem;
  color: #2563eb;
  animation: spin 1.5s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0.4) 40%, rgba(0, 0, 0, 0.1) 100%);
  z-index: 2;
}

.banner-text {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 3rem;
  color: #ffffff;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.6);
  z-index: 3;
  animation: slideUp 1s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.banner-title {
  margin: 0 0 0.5rem;
  font-size: 2rem;
  font-weight: 700;
  line-height: 1.2;
  max-width: 800px;
  animation: fadeIn 1.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.banner-subtitle {
  margin: 0 0 1.5rem;
  font-size: 1.1rem;
  line-height: 1.4;
  max-width: 600px;
  opacity: 0.9;
  animation: fadeIn 1.5s ease;
}

.banner-button {
  animation: fadeIn 1.8s ease;
  border-radius: 999px;
  padding: 0.75rem 2rem;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.banner-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.4);
}

.banner-empty {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  background: linear-gradient(135deg, #eef2ff, #fdf2ff);
  color: #6b7280;
}

.empty-content {
  text-align: center;
  padding: 2rem;
}

.empty-icon {
  font-size: 4rem;
  color: #2563eb;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-content h4 {
  margin: 0 0 0.5rem;
  font-size: 1.25rem;
  font-weight: 600;
  color: #4b5563;
}

.empty-content p {
  margin: 0;
  font-size: 1rem;
  color: #9ca3af;
}

/* 自定义轮播指示器样式 */
:deep(.el-carousel__indicators) {
  bottom: -30px;
}

:deep(.el-carousel__button) {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #e5e7eb;
  transition: all 0.3s ease;
  margin: 0 6px;
}

:deep(.el-carousel__button:hover) {
  background: #9ca3af;
  transform: scale(1.2);
}

:deep(.is-active .el-carousel__button) {
  background: #2563eb;
  width: 30px;
  border-radius: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-item {
    height: 220px;
  }
  
  .banner-text {
    padding: 2rem;
  }
  
  .banner-title {
    font-size: 1.5rem;
  }
  
  .banner-subtitle {
    font-size: 0.9rem;
  }
  
  .banner-button {
    padding: 0.5rem 1.5rem;
    font-size: 0.9rem;
  }
  
  .banner-empty {
    height: 220px;
  }
  
  .empty-icon {
    font-size: 3rem;
  }
}

@media (max-width: 480px) {
  .banner-item {
    height: 180px;
  }
  
  .banner-text {
    padding: 1.5rem;
  }
  
  .banner-title {
    font-size: 1.2rem;
  }
  
  .banner-subtitle {
    font-size: 0.8rem;
    margin-bottom: 1rem;
  }
  
  .banner-button {
    padding: 0.4rem 1.2rem;
    font-size: 0.8rem;
  }
  
  .banner-empty {
    height: 180px;
  }
  
  .empty-icon {
    font-size: 2rem;
  }
  
  .empty-content h4 {
    font-size: 1rem;
  }
  
  .empty-content p {
    font-size: 0.8rem;
  }
}
</style>

