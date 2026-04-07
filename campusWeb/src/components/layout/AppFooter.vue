<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="left">
        <div class="brand">
          <div class="brand-name">校园二手交易平台</div>
          <div class="brand-slogan">让闲置物品找到新主人</div>
        </div>
        <div class="meta">
          <span v-if="config?.notice" class="notice">{{ config.notice }}</span>
          <span v-if="config?.copyright" class="copyright">
            {{ config.copyright }}
          </span>
        </div>
      </div>
      <div class="cols">
        <div class="col">
          <div class="col-title">帮助中心</div>
          <a href="javascript:;" class="link" @click="goRules">
            <el-icon class="link-icon"><Document /></el-icon>
            交易须知
          </a>
          <a href="javascript:;" class="link" @click="goPrivacy">
            <el-icon class="link-icon"><Lock /></el-icon>
            隐私政策
          </a>
          <a href="javascript:;" class="link" @click="goNotice">
            <el-icon class="link-icon"><Bell /></el-icon>
            公告中心
          </a>
        </div>
        <div class="col">
          <div class="col-title">联系我们</div>
          <a href="javascript:;" class="link" v-if="config?.serviceContact">
            <el-icon class="link-icon"><Phone /></el-icon>
            客服：{{ config.serviceContact }}
          </a>
          <a href="javascript:;" class="link">
            <el-icon class="link-icon"><Timer /></el-icon>
            服务时间：9:00-22:00
          </a>
        </div>
        <div class="col">
          <div class="col-title">关注我们</div>
          <a href="javascript:;" class="link">
            <el-icon class="link-icon"><ChatLineSquare /></el-icon>
            微信公众号
          </a>
          <a href="javascript:;" class="link">
            <el-icon class="link-icon"><Message /></el-icon>
            官方QQ群
          </a>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <div class="bottom-content">
        <span>© 2024 校园二手交易平台 版权所有</span>
        <span class="separator">|</span>
        <span>备案号：京ICP备12345678号</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { Document, Lock, Bell, Phone, Timer, ChatLineSquare, Message } from '@element-plus/icons-vue';

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
  background: #f9fafb;
  margin-top: 3rem;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(0, 3fr);
  gap: 2rem;
}

.left {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.brand {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.brand-name {
  font-weight: 700;
  font-size: 1.5rem;
  background: linear-gradient(90deg, #2563eb 40%, #60a5fa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  cursor: pointer;
  letter-spacing: 1px;
  text-shadow: 0 2px 8px rgba(37,99,235,0.08);
}

.brand-slogan {
  font-size: 1rem;
  color: #6b7280;
  line-height: 1.4;
}

.meta {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.875rem;
  color: #9ca3af;
}

.cols {
  display: flex;
  gap: 3rem;
  justify-content: flex-end;
}

.col {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.col-title {
  font-weight: 600;
  font-size: 1rem;
  color: #111827;
  margin-bottom: 0.25rem;
  position: relative;
  padding-bottom: 0.5rem;
}

.col-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 20px;
  height: 2px;
  background: linear-gradient(90deg, #2563eb, #60a5fa);
  border-radius: 2px;
}

.link {
  color: #6b7280;
  text-decoration: none;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  padding: 0.25rem 0;
  border-radius: 4px;
}

.link:hover {
  color: #2563eb;
  transform: translateX(4px);
}

.link-icon {
  font-size: 14px;
  transition: color 0.3s ease;
}

.link:hover .link-icon {
  color: #2563eb;
}

.footer-bottom {
  border-top: 1px solid #e5e7eb;
  background: #ffffff;
  padding: 1rem 0;
}

.bottom-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  font-size: 0.8rem;
  color: #9ca3af;
}

.separator {
  color: #e5e7eb;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-inner {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .cols {
    justify-content: flex-start;
    flex-wrap: wrap;
    gap: 2rem;
  }
  
  .col {
    flex: 1 1 150px;
  }
  
  .bottom-content {
    flex-direction: column;
    gap: 0.5rem;
    text-align: center;
  }
  
  .separator {
    display: none;
  }
}

@media (max-width: 480px) {
  .footer-inner {
    padding: 1.5rem 1rem;
  }
  
  .brand-name {
    font-size: 1.25rem;
  }
  
  .cols {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .col {
    flex: 1 1 100%;
  }
}
</style>

