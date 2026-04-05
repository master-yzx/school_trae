<template>
  <div class="section-title">
    <div class="main">
      <h2>{{ title }}</h2>
      <p v-if="subtitle">{{ subtitle }}</p>
    </div>
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
  margin-bottom: 1rem;
}

.section-title h2 {
  margin: 0;
  font-size: 1.25rem;
}

.section-title p {
  margin: 0.25rem 0 0;
  font-size: 0.875rem;
  color: #6b7280;
}
</style>

