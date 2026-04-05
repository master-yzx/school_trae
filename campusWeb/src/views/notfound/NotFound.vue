<template>
  <AppLayout>
    <main class="main">
      <el-result
        icon="warning"
        title="页面不存在"
        sub-title="你访问的页面可能已被删除或地址有误"
      >
        <template #extra>
          <el-button type="primary" @click="goHome">返回首页</el-button>
          <el-button @click="goBack">返回上一页</el-button>
        </template>
      </el-result>
    </main>
  </AppLayout>
</template>

<script setup>
import { useRouter } from 'vue-router';
import AppLayout from '../../components/layout/AppLayout.vue';

const router = useRouter();

function goHome() {
  router.push('/');
}

function goBack() {
  router.back();
}
</script>

<style scoped>
.main {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1rem 3rem;
}
</style>

