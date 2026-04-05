<template>
  <div class="auth-page">
    <div class="auth-page-inner">
      <span class="auth-brand">校园二手交易平台</span>
      <section class="auth-card">
        <SectionTitle title="重置密码" subtitle="通过手机号找回密码（未接入短信时验证码可留空）" />

        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <div class="inline">
              <el-input v-model="form.code" placeholder="未接入短信，可留空" />
              <el-tooltip content="暂未接入短信服务" placement="top">
                <el-button class="ml8" disabled>获取验证码</el-button>
              </el-tooltip>
            </div>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="form.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <div class="form-footer">
              <el-button type="primary" @click="handleSubmit">提交重置</el-button>
              <el-button link type="primary" @click="goLogin">返回登录</el-button>
            </div>
          </el-form-item>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';

const router = useRouter();

const formRef = ref();
const form = ref({
  phone: '',
  code: '',
  newPassword: '',
  confirmPassword: '',
});

const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [], /* 未接入短信服务，验证码选填 */
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }],
};

const sending = ref(false);

function goLogin() {
  router.push({ name: 'Auth' });
}

function sendCode() {
  if (!form.value.phone) {
    ElMessage.warning('请先填写手机号');
    return;
  }
  sending.value = true;
  request
    .post('/auth/captcha', {
      phone: form.value.phone,
      type: 'RESET',
    })
    .then(() => {
      ElMessage.success('验证码已发送');
    })
    .finally(() => {
      sending.value = false;
    });
}

function handleSubmit() {
  formRef.value.validate(async (valid) => {
    if (!valid) return;
    try {
      await request.post('/auth/reset-password', form.value);
      ElMessage.success('密码重置成功，请重新登录');
      router.push({ name: 'Auth' });
    } catch (e) {
      ElMessage.warning(e?.response?.data?.message || '重置失败，请稍后重试');
    }
  });
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: #f5f7fb;
  background-image:
    linear-gradient(rgba(30, 58, 95, 0.45), rgba(15, 23, 42, 0.5)),
    url(https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=1920&q=80);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
}

.auth-page-inner {
  width: 100%;
  max-width: 420px;
}

.auth-brand {
  display: block;
  text-align: center;
  font-size: 1.25rem;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  margin-bottom: 1.5rem;
}

.auth-card {
  background: rgba(255, 255, 255, 0.72);
  border-radius: 14px;
  padding: 1.5rem 1.75rem 1.75rem;
  border: 1px solid rgba(255, 255, 255, 0.35);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.auth-card :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.55);
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  transition: background 0.2s ease, box-shadow 0.2s ease;
}
.auth-card :deep(.el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
}
.auth-card :deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.35);
}
.auth-card :deep(.el-form-item__label) {
  color: rgba(0, 0, 0, 0.72);
}
.auth-card :deep(.el-input__inner::placeholder) {
  color: rgba(55, 65, 81, 0.6);
}

.form-footer {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.inline {
  display: flex;
  align-items: center;
}

.ml8 {
  margin-left: 8px;
}
</style>

