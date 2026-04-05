<template>
  <AppLayout>
    <main class="main" v-if="profile">
      <section class="section">
        <SectionTitle title="头像管理" />
        <div class="avatar-row">
          <el-avatar :size="80" :src="profile.avatarUrl" />
          <el-upload
            class="upload"
            :show-file-list="false"
            :http-request="handleAvatarUpload"
          >
            <el-button>上传新头像</el-button>
          </el-upload>
        </div>
      </section>

      <section class="section">
        <SectionTitle title="基础信息" />
        <el-form ref="infoFormRef" :model="infoForm" label-width="80px">
          <el-form-item label="昵称">
            <el-input v-model="infoForm.nickname" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="infoForm.phone" />
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="infoForm.studentNo" />
          </el-form-item>
          <el-form-item label="院校">
            <el-input v-model="infoForm.campusName" />
          </el-form-item>
          <el-form-item label="店铺简介" v-if="profile.role === 'SELLER'">
            <el-input v-model="infoForm.shopIntro" type="textarea" rows="3" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveInfo">保存</el-button>
            <el-button @click="resetInfo">取消</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="section">
        <SectionTitle title="密码修改" />
        <el-form ref="pwdFormRef" :model="pwdForm" label-width="80px">
          <el-form-item label="原密码">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="changePassword">提交修改</el-button>
            <el-button @click="resetPasswordForm">取消</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="section">
        <el-button @click="goBack">返回个人中心</el-button>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const profile = ref(null);
const infoFormRef = ref();
const pwdFormRef = ref();

const infoForm = ref({
  nickname: '',
  phone: '',
  studentNo: '',
  campusName: '',
  shopIntro: '',
});

const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

function fillInfoFromProfile() {
  if (!profile.value) return;
  infoForm.value.nickname = profile.value.nickname || '';
  infoForm.value.phone = profile.value.phone || '';
  infoForm.value.studentNo = profile.value.studentNo || '';
  infoForm.value.campusName = profile.value.campusName || '';
  infoForm.value.shopIntro = profile.value.shopIntro || '';
}

async function loadProfile() {
  const res = await request.get('/user/profile');
  profile.value = res.data || null;
  fillInfoFromProfile();
}

async function handleAvatarUpload(option) {
  try {
    const formData = new FormData();
    formData.append('file', option.file);
    const res = await request.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    const data = res.data || {};
    if (data.url) {
      await request.post('/user/profile/avatar', { avatarUrl: data.url });
      profile.value = profile.value || {};
      profile.value.avatarUrl = data.url;
      if (authStore.accessToken) {
        authStore.setAuth({
          accessToken: authStore.accessToken,
          user: {
            ...(authStore.user || {}),
            avatarUrl: data.url,
          },
        });
      }
      ElMessage.success('头像已更新');
    }
    option.onSuccess && option.onSuccess(res, option.file);
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '头像上传失败');
    option.onError && option.onError(e);
  }
}

async function saveInfo() {
  await request.post('/user/profile/update', infoForm.value);
  ElMessage.success('信息已保存');
}

function resetInfo() {
  fillInfoFromProfile();
}

async function changePassword() {
  await request.post('/user/password/change', pwdForm.value);
  ElMessage.success('密码修改成功，请使用新密码重新登录');
  resetPasswordForm();
}

function resetPasswordForm() {
  pwdForm.value.oldPassword = '';
  pwdForm.value.newPassword = '';
  pwdForm.value.confirmPassword = '';
}

function goBack() {
  router.push({ name: 'ProfileHome' });
}

loadProfile();
</script>

<style scoped>
.main {
  max-width: 720px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1.25rem;
}

.avatar-row {
  display: flex;
  align-items: center;
  gap: 1rem;
}
</style>

