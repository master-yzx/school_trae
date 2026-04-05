<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="left">
        <div class="brand">校园二手交易平台</div>
        <div class="meta">
          <span v-if="config?.notice">{{ config.notice }}</span>
          <span v-if="config?.copyright">
            · {{ config.copyright }}
          </span>
        </div>
      </div>
      <div class="cols">
        <div class="col">
          <div class="col-title">帮助</div>
          <a href="javascript:;" class="link" @click="goRules">
            交易须知
          </a>
          <a href="javascript:;" class="link" @click="goPrivacy">
            隐私政策
          </a>
          <a href="javascript:;" class="link" @click="goNotice">
            公告中心
          </a>
        </div>
        <div class="col">
          <div class="col-title">联系我们</div>
          <span v-if="config?.serviceContact" class="link">
            客服：{{ config.serviceContact }}
          </span>
          <span class="link">服务时间：9:00-22:00</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { useRouter } from 'vue-router';

defineProps({
  config: {
    type: Object,
    default: () => ({}),
  },
});

const router = useRouter();

function goRules() {
  router.push({ name: 'TradeRules' });
}

function goPrivacy() {
  router.push({ name: 'PrivacyPolicy' });
}

function goNotice() {
  router.push({ name: 'NoticeCenter' });
}
</script>

<style scoped>
.footer {
  border-top: 1px solid #e5e7eb;
  background: #ffffff;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.75rem 1rem;
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(0, 3fr);
  gap: 1.5rem;
  font-size: 13px;
  color: #6b7280;
}

.brand {
  font-weight: 600;
  margin-bottom: 0.125rem;
}

.cols {
  display: flex;
  gap: 2.5rem;
  justify-content: flex-end;
}

.col {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.col-title {
  font-weight: 600;
  margin-bottom: 0.15rem;
}

.link {
  color: #6b7280;
  text-decoration: none;
}

.link:hover {
  color: #2563eb;
}
</style>

