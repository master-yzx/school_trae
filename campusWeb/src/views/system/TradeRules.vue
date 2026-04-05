<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="交易须知" subtitle="安全交易、文明沟通、风险提示" />
      <el-card shadow="never">
        <div class="content">
          <p>1. 建议优先同校当面交易，现场验货再付款。</p>
          <p>2. 邮寄交易请使用可追踪物流，并保留聊天/转账凭证。</p>
          <p>3. 禁止发布违禁品、盗版资源等内容，违规将被处理。</p>
          <p>4. 交易纠纷请先协商，必要时联系平台客服。</p>
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

