<template>
  <div class="auth-page">
    <div class="auth-page-inner">
      <span class="auth-brand">校园二手交易平台</span>
      <section class="auth-card">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="登录" name="login">
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="80px">
              <el-form-item label="账号" prop="account">
                <el-input v-model="loginForm.account" placeholder="手机号 / 学号" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="loginForm.password" type="password" show-password />
              </el-form-item>
              <el-form-item label="验证码" prop="captcha">
                <div class="captcha-row">
                  <el-input
                    v-model="loginForm.captcha"
                    class="captcha-input"
                    maxlength="4"
                    placeholder="请输入右侧验证码"
                  />
                  <el-image
                    v-if="captchaImage"
                    :src="captchaImage"
                    class="captcha-image"
                    fit="fill"
                    @click="refreshCaptcha"
                  />
                  <div v-else class="captcha-placeholder" @click="refreshCaptcha">
                    点击获取
                  </div>
                </div>
              </el-form-item>
              <el-form-item>
                <div class="form-footer">
                  <el-button type="primary" @click="handleLogin">登录</el-button>
                  <el-button link type="primary" @click="goForgot">忘记密码？</el-button>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="注册" name="register">
            <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="80px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="registerForm.username" placeholder="请输入用户名" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="registerForm.phone" />
              </el-form-item>
              <el-form-item label="验证码" prop="code">
                <div class="inline">
                  <el-input v-model="registerForm.code" placeholder="未接入短信，可留空" />
                  <el-tooltip content="暂未接入短信服务" placement="top">
                    <el-button class="ml8" disabled>获取验证码</el-button>
                  </el-tooltip>
                </div>
              </el-form-item>
              <el-form-item label="学号">
                <el-input v-model="registerForm.studentNo" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="registerForm.password" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="registerForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="角色" prop="role">
                <el-radio-group v-model="registerForm.role">
                  <el-radio-button label="USER">普通用户</el-radio-button>
                  <el-radio-button label="SELLER">卖家</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-checkbox v-model="agree">已阅读并同意平台协议</el-checkbox>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleRegister">注册</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const activeTab = ref('login');

const loginFormRef = ref();
const registerFormRef = ref();

const loginForm = ref({
  account: '',
  password: '',
  captcha: '',
  captchaId: '',
});

const loginRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
};

const registerForm = ref({
  username: '',
  phone: '',
  code: '',
  studentNo: '',
  password: '',
  confirmPassword: '',
  role: 'USER',
});

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [], /* 未接入短信服务，验证码选填 */
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }],
};

const agree = ref(true);
const sending = ref(false);
const captchaImage = ref('');

async function refreshCaptcha() {
  try {
    const res = await request.get('/auth/login-captcha');
    const data = res.data || {};
    loginForm.value.captchaId = data.captchaId || '';
    captchaImage.value = data.imageData || '';
  } catch (e) {
    captchaImage.value = '';
  }
}

onMounted(() => {
  refreshCaptcha();
});

function goForgot() {
  router.push({ name: 'ForgotPassword' });
}

async function syncProfileAfterLogin() {
  try {
    const res = await request.get('/user/profile');
    const p = res?.data || null;
    if (!p) return;
    authStore.setAuth({
      accessToken: authStore.accessToken,
      user: {
        ...(authStore.user || {}),
        id: p.id ?? authStore.user?.id,
        nickname: p.nickname || authStore.user?.nickname,
        role: p.role || authStore.user?.role,
        avatarUrl: p.avatarUrl || authStore.user?.avatarUrl,
      },
    });
  } catch (e) {
    // ignore, 保持登录态不变
  }
}

function handleLogin() {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return;
    const payload = {
      account: loginForm.value.account,
      password: loginForm.value.password,
      captcha: loginForm.value.captcha,
      captchaId: loginForm.value.captchaId,
    };
    const res = await request.post('/auth/login', payload);
    if (res.code !== 0 || !res.data) {
      ElMessage.error(res.message || '登录失败，请检查账号、密码或验证码');
      await refreshCaptcha();
      return;
    }
    const data = res.data || {};
    authStore.setAuth({
      accessToken: data.accessToken,
      user: {
        id: data.userId,
        nickname: data.nickname,
        role: data.role,
      },
    });
    await syncProfileAfterLogin();
    ElMessage.success('登录成功');
    const redirect = route.query?.redirect;
    router.push(typeof redirect === 'string' && redirect ? redirect : '/');
  });
}

function sendRegisterCode() {
  if (!registerForm.value.phone) {
    ElMessage.warning('请先填写手机号');
    return;
  }
  sending.value = true;
  request
    .post('/auth/captcha', {
      phone: registerForm.value.phone,
      type: 'REGISTER',
    })
    .then(() => {
      ElMessage.success('验证码已发送');
    })
    .finally(() => {
      sending.value = false;
    });
}

function handleRegister() {
  if (!agree.value) {
    ElMessage.warning('请先勾选同意平台协议');
    return;
  }
  registerFormRef.value.validate(async (valid) => {
    if (!valid) return;
    const res = await request.post('/auth/register', registerForm.value);
    if (!res || res.code !== 0) {
      ElMessage.warning(res?.message || '注册失败，请稍后重试');
      return;
    }
    ElMessage.success('注册成功，请登录');
    activeTab.value = 'login';
  });
}

function applyTabFromRoute() {
  const tab = route.query?.tab;
  if (tab === 'register') activeTab.value = 'register';
  if (tab === 'login') activeTab.value = 'login';
}

onMounted(() => {
  applyTabFromRoute();
  refreshCaptcha();
});

watch(
  () => route.query?.tab,
  () => {
    applyTabFromRoute();
  },
);
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

.form-footer {
  display: flex;
  justify-content: space-between;
  width: 100%;
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

/* 输入框与卡片风格统一，弱化突兀感 */
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
.auth-card :deep(.el-input__inner),
.auth-card :deep(.el-input__inner::placeholder) {
  color: #374151;
}
.auth-card :deep(.el-input__inner::placeholder) {
  color: rgba(55, 65, 81, 0.6);
}
.auth-card .captcha-placeholder {
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  color: rgba(55, 65, 81, 0.7);
}
.auth-card .captcha-image {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.inline {
  display: flex;
  align-items: center;
}

.ml8 {
  margin-left: 8px;
}

.captcha-row {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.captcha-input {
  width: 90px;
}

.captcha-box {
  min-width: 72px;
  padding: 4px 8px;
  border-radius: 6px;
  background: repeating-linear-gradient(
    -45deg,
    #f9fafb,
    #f9fafb 6px,
    #e5e7eb 6px,
    #e5e7eb 12px
  );
  font-weight: 600;
  letter-spacing: 2px;
  color: #111827;
  text-align: center;
}

.captcha-hint {
  font-size: 0.8rem;
  color: #9ca3af;
}
</style>

