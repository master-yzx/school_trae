<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="left">
        <span class="title">系统配置</span>
        <span class="sub">平台基础配置与安全设置</span>
      </div>
      <div class="right">
        <el-button type="primary" @click="goBack">返回后台首页</el-button>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :span="14">
        <el-card shadow="never" class="config-card">
          <template #header>平台基础配置</template>
          <el-form :model="form" label-width="110px">
            <el-form-item label="平台名称">
              <el-input v-model="form.platformName" />
            </el-form-item>
            <el-form-item label="首页公告">
              <el-input v-model="form.homepageNotice" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="版权信息">
              <el-input v-model="form.copyrightText" />
            </el-form-item>
            <el-form-item label="客服联系方式">
              <el-input v-model="form.serviceContact" />
            </el-form-item>
            <el-form-item label="交易须知">
              <el-input v-model="form.tradeRules" type="textarea" :rows="4" />
            </el-form-item>
            <el-form-item label="违规规则">
              <el-input v-model="form.violationRules" type="textarea" :rows="4" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveConfig">保存配置</el-button>
              <el-button @click="loadConfig">取消修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="config-card">
          <template #header>缓存与安全</template>
          <el-form label-width="110px">
            <el-form-item label="密码复杂度">
              <el-input v-model="passwordRule" placeholder="例如：至少 8 位，包含数字和字母" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="savePasswordRule">保存密码规则</el-button>
              <el-button type="danger" @click="clearCache">清空 Redis 缓存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="never" class="config-card">
          <template #header>Logo 管理</template>
          <div class="logo-box">
            <div class="logo-preview">LOGO 预览</div>
            <el-upload
              class="upload-area"
              :http-request="handleLogoUpload"
              :show-file-list="false"
            >
              <el-button type="primary">上传 Logo</el-button>
            </el-upload>
          </div>
        </el-card>

        <el-card shadow="never" class="config-card">
          <template #header>校园管理</template>
          <div class="toolbar">
            <el-button type="primary" size="small" @click="openCampusDialog()">新增校园</el-button>
          </div>
          <el-table :data="campusList" height="260" border>
            <el-table-column prop="name" label="校园名称" />
            <el-table-column prop="city" label="城市" width="120" />
            <el-table-column prop="enabled" label="启用" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.enabled ? 'success' : 'info'">
                  {{ scope.row.enabled ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140">
              <template #default="scope">
                <el-button size="small" type="primary" link @click="openCampusDialog(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" link @click="deleteCampus(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="campusVisible" :title="campusForm.id ? '编辑校园' : '新增校园'" width="420px">
      <el-form :model="campusForm" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="campusForm.name" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="campusForm.city" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="campusForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="campusVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveCampus">保 存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();

const form = ref({
  platformName: '',
  homepageNotice: '',
  copyrightText: '',
  serviceContact: '',
  tradeRules: '',
  violationRules: '',
});

const passwordRule = ref('');
const campusList = ref([]);

const campusVisible = ref(false);
const campusForm = ref({
  id: null,
  name: '',
  city: '',
  enabled: true,
});

const loadConfig = async () => {
  const res = await request.get('/admin/settings');
  if (res.code === 0 && res.data) {
    form.value = res.data;
  }
};

const saveConfig = async () => {
  await request.post('/admin/settings', form.value);
  ElMessage.success('已保存平台配置');
};

const loadCampus = async () => {
  const res = await request.get('/admin/settings/campus');
  if (res.code === 0) {
    campusList.value = res.data || [];
  }
};

const openCampusDialog = (row) => {
  if (row) {
    campusForm.value = { ...row };
  } else {
    campusForm.value = {
      id: null,
      name: '',
      city: '',
      enabled: true,
    };
  }
  campusVisible.value = true;
};

const saveCampus = async () => {
  await request.post('/admin/settings/campus', campusForm.value);
  ElMessage.success('已保存校园信息');
  campusVisible.value = false;
  loadCampus();
};

const deleteCampus = (row) => {
  ElMessageBox.confirm(`确定删除校园【${row.name}】吗？`, '提示', {
    type: 'warning',
  }).then(async () => {
    await request.delete(`/admin/settings/campus/${row.id}`);
    ElMessage.success('已删除校园');
    loadCampus();
  }).catch(() => {});
};

const clearCache = async () => {
  await request.post('/admin/settings/cache/clear');
  ElMessage.success('已清空 Redis 缓存');
};

const savePasswordRule = async () => {
  await request.post('/admin/settings/security', null, {
    params: { passwordRule: passwordRule.value },
  });
  ElMessage.success('已保存密码复杂度规则');
};

const handleLogoUpload = async (option) => {
  try {
    const fd = new FormData();
    fd.append('file', option.file);
    const res = await request.post('/admin/settings/logo', fd, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    if (res.data && res.data.url) {
      form.value.logoUrl = res.data.url;
      ElMessage.success('Logo 上传成功');
    } else {
      ElMessage.success('Logo 已上传');
    }
    option.onSuccess && option.onSuccess(res, option.file);
  } catch (e) {
    ElMessage.error('Logo 上传失败');
    option.onError && option.onError(e);
  }
};

const goBack = () => {
  router.push('/admin/dashboard');
};

onMounted(() => {
  loadConfig();
  loadCampus();
});
</script>

<style scoped>
.admin-page {
  padding: 16px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header .title {
  font-size: 18px;
  font-weight: 600;
  margin-right: 8px;
}
.page-header .sub {
  color: #909399;
}
.config-card {
  margin-bottom: 16px;
}
.logo-box {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.logo-preview {
  width: 160px;
  height: 80px;
  border: 1px dashed #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  margin-bottom: 12px;
}
.toolbar {
  margin-bottom: 8px;
  text-align: right;
}
</style>

