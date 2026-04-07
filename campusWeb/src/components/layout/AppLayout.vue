<template>
  <div class="page">
    <AppHeader
      :categories="appStore.categories"
      :hot-searches="appStore.hotSearches"
      :campuses="appStore.campuses"
    />

    <main class="main">
      <slot />
    </main>

    <AppFooter :config="appStore.footerConfig" />
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import AppHeader from './AppHeader.vue';
import AppFooter from './AppFooter.vue';
import { useAppStore } from '../../stores/app';

const appStore = useAppStore();

onMounted(() => {
  appStore.loadBaseData();
});
</script>

<style scoped>
.page {
  background: #fff;
}

.main {
  width: 100%;
}
</style>

