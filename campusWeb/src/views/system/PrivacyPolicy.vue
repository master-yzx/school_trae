<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="隐私政策" subtitle="我们如何收集、使用与保护你的信息" />
      <el-card shadow="never">
        <div class="content">
          <p>1. 我们仅在提供服务所必需的范围内收集信息（如账号、联系方式）。</p>
          <p>2. 未经你的明确授权，我们不会向第三方出售你的个人信息。</p>
          <p>3. 你可以在账号设置中修改资料并管理登录状态。</p>
          <p>4. 如需删除账号/数据，请联系平台客服。</p>
        </div>
      </el-card>
    </main>
  </AppLayout>
</template>

<script setup>
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
</script>

<style scoped>
.main {
  max-width: 900px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}
.content {
  font-size: 0.95rem;
  line-height: 1.8;
  color: #374151;
}
</style>

