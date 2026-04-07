<template>
  <div class="section-title">
    <div class="main">
      <div class="title-container">
        <h2 class="title">{{ title }}</h2>
        <div class="title-decoration"></div>
      </div>
      <p v-if="subtitle" class="subtitle">{{ subtitle }}</p>
    </div>
    <slot></slot>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  subtitle: {
    type: String,
    default: '',
  },
});
</script>

<style scoped>
.section-title {
  margin-bottom: 2rem;
  position: relative;
  overflow: hidden;
}

.main {
  position: relative;
  z-index: 1;
}

.title-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.title {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 700;
  color: #111827;
  position: relative;
  display: inline-block;
  animation: slideInLeft 0.8s ease;
}

.title::before {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #2563eb, #60a5fa);
  transition: width 0.3s ease;
  border-radius: 3px;
}

.section-title:hover .title::before {
  width: 100%;
}

.title-decoration {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, #e5e7eb, transparent);
  animation: fadeIn 1s ease 0.3s both;
}

.subtitle {
  margin: 0.5rem 0 0;
  font-size: 1rem;
  color: #6b7280;
  line-height: 1.4;
  animation: fadeIn 1s ease 0.5s both;
}

/* 动画效果 */
@keyframes slideInLeft {
  from {
    transform: translateX(-20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .title {
    font-size: 1.5rem;
  }
  
  .subtitle {
    font-size: 0.9rem;
  }
  
  .section-title {
    margin-bottom: 1.5rem;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 1.25rem;
  }
  
  .subtitle {
    font-size: 0.8rem;
  }
  
  .section-title {
    margin-bottom: 1.25rem;
  }
}
</style>

