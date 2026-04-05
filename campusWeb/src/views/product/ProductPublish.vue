<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="发布商品" />

      <el-steps :active="step" finish-status="success" class="steps">
        <el-step title="基础信息" />
        <el-step title="媒体素材" />
        <el-step title="商品详情" />
      </el-steps>

      <section v-if="step === 0" class="section">
        <el-form ref="baseFormRef" :model="baseForm" label-width="90px">
          <el-form-item label="标题">
            <el-input v-model="baseForm.title" />
          </el-form-item>
          <el-form-item label="分类">
            <el-cascader
              v-model="categoryPath"
              :options="appStore.categories"
              :props="cascaderProps"
              placeholder="请选择分类（最多三级）"
              clearable
            />
          </el-form-item>
          <el-form-item label="价格">
            <el-input-number v-model="baseForm.price" :min="0" />
          </el-form-item>
          <el-form-item label="新旧程度">
            <el-select v-model="baseForm.condition">
              <el-option label="九成新" value="NINE" />
              <el-option label="八成新" value="EIGHT" />
            </el-select>
          </el-form-item>
          <el-form-item label="交易方式">
            <el-select v-model="baseForm.deliveryType">
              <el-option label="自提" value="SELF_PICKUP" />
              <el-option label="邮寄" value="SHIPPING" />
            </el-select>
          </el-form-item>
          <el-form-item label="包邮">
            <el-switch v-model="baseForm.freeShipping" />
          </el-form-item>
          <el-form-item label="院校">
            <el-input v-model="baseForm.campusName" />
          </el-form-item>
          <el-form-item label="自提地点">
            <el-input v-model="baseForm.pickupLocation" />
          </el-form-item>
        </el-form>
      </section>

      <section v-else-if="step === 1" class="section">
        <SectionTitle title="媒体素材" subtitle="上传商品图片（可多张）和可选视频" />
        <el-upload
          class="upload-block"
          list-type="picture-card"
          :auto-upload="true"
          :http-request="handleMediaUpload"
          :file-list="fileList"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
        <p class="hint">图片会上传到后端 upload 文件夹，系统会自动使用首张图作为封面。</p>
      </section>

      <section v-else class="section">
        <SectionTitle title="商品详情" subtitle="填写使用时长、成色说明、瑕疵说明、转让原因等" />
        <el-input
          v-model="detailForm.descriptionHtml"
          type="textarea"
          rows="10"
          placeholder="支持简单富文本说明，后续可替换为富文本编辑器组件"
        />
      </section>

      <section class="section footer-actions">
        <el-button @click="prevStep" :disabled="step === 0">上一步</el-button>
        <el-button type="primary" @click="nextStep" v-if="step < 2">下一步</el-button>
        <el-button @click="saveDraft">保存草稿</el-button>
        <el-button type="success" @click="submit">提交审核</el-button>
        <el-button @click="goBack">取消</el-button>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';
import { useAppStore } from '../../stores/app';

const router = useRouter();
const route = useRoute();
const appStore = useAppStore();

const step = ref(0);
const baseFormRef = ref();

const baseForm = ref({
  title: '',
  categoryId: null,
  price: 0,
  condition: 'NINE',
  deliveryType: 'SELF_PICKUP',
  freeShipping: false,
  campusName: '',
  pickupLocation: '',
});

const detailForm = ref({
  descriptionHtml: '',
});

const fileList = ref([]);
const imageUrls = ref([]);
const categoryPath = ref([]);
const cascaderProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  expandTrigger: 'hover',
  checkStrictly: true, // 允许选择任意层级，只校验至少选择一级
};

function prevStep() {
  if (step.value > 0) step.value -= 1;
}

function nextStep() {
  if (step.value < 2) step.value += 1;
}

async function saveDraft() {
  const payload = {
    ...baseForm.value,
    ...detailForm.value,
    coverUrl: imageUrls.value[0] || null,
  };
  await request.post('/seller/products/draft', payload);
  ElMessage.success('草稿已保存');
}

async function submit() {
  if (!baseForm.value.title?.trim()) {
    ElMessage.warning('请输入商品标题');
    step.value = 0;
    return;
  }
  if (!categoryPath.value.length) {
    ElMessage.warning('请选择商品分类');
    step.value = 0;
    return;
  }
  if (!baseForm.value.price || baseForm.value.price <= 0) {
    ElMessage.warning('请输入正确的价格');
    step.value = 0;
    return;
  }
  if (!baseForm.value.condition) {
    ElMessage.warning('请选择新旧程度');
    step.value = 0;
    return;
  }
  if (!baseForm.value.deliveryType) {
    ElMessage.warning('请选择交易方式');
    step.value = 0;
    return;
  }
  if (!baseForm.value.campusName?.trim()) {
    ElMessage.warning('请输入院校名称');
    step.value = 0;
    return;
  }
  if (!imageUrls.value.length) {
    ElMessage.warning('请至少上传一张商品图片');
    step.value = 1;
    return;
  }
  if (!detailForm.value.descriptionHtml?.trim()) {
    ElMessage.warning('请填写商品详情说明');
    step.value = 2;
    return;
  }

  // 使用最后一级作为实际分类 ID（如果只选一级/二级，就用当前选中的那一级）
  baseForm.value.categoryId = categoryPath.value[categoryPath.value.length - 1] || null;

  const payload = {
    ...baseForm.value,
    ...detailForm.value,
    coverUrl: imageUrls.value[0] || null,
  };
  await request.post('/seller/products/submit', payload);
  ElMessage.success('已提交审核');
  router.push({ name: 'SellerProductList' });
}

function goBack() {
  router.push({ name: 'SellerProductList' });
}

async function handleMediaUpload(option) {
  try {
    const formData = new FormData();
    formData.append('file', option.file);
    const res = await request.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    const data = res.data || {};
    if (data.url) {
      imageUrls.value.push(data.url);
    }
    option.onSuccess && option.onSuccess(res, option.file);
  } catch (e) {
    ElMessage.error('图片上传失败');
    option.onError && option.onError(e);
  }
}

async function loadForEdit() {
  const id = route.query.id;
  const numericId = id != null ? Number(id) : NaN;
  if (!Number.isFinite(numericId) || numericId <= 0) return;

  const res = await request.get(`/seller/products/${numericId}`);
  const data = res.data || {};
  if (!data || !data.id) return;

  baseForm.value.title = data.title || '';
  baseForm.value.categoryId = data.categoryId || null;
  baseForm.value.price = data.price || 0;
  baseForm.value.condition = data.condition || 'NINE';
  baseForm.value.deliveryType = data.deliveryType || 'SELF_PICKUP';
  baseForm.value.freeShipping = data.freeShipping ?? false;
  baseForm.value.campusName = data.campusName || '';
  baseForm.value.pickupLocation = data.pickupLocation || '';

  detailForm.value.descriptionHtml = data.descriptionHtml || '';

  imageUrls.value = [];
  fileList.value = [];
  if (data.coverUrl) {
    imageUrls.value.push(data.coverUrl);
    fileList.value.push({
      name: '封面图',
      url: data.coverUrl,
    });
  }

  // 把 id 带回提交 payload，后端据此做更新
  baseForm.value.id = data.id;
}

function findCategoryPath(nodes, targetId, path = []) {
  for (const n of nodes || []) {
    const nextPath = [...path, n.id];
    if (n.id === targetId) {
      return nextPath;
    }
    if (n.children && n.children.length) {
      const found = findCategoryPath(n.children, targetId, nextPath);
      if (found && found.length) return found;
    }
  }
  return null;
}

onMounted(async () => {
  await appStore.loadBaseData();
  await loadForEdit();

  if (baseForm.value.categoryId && appStore.categories?.length) {
    const path = findCategoryPath(appStore.categories, baseForm.value.categoryId);
    if (path && path.length) {
      categoryPath.value = path;
    }
  }
});
</script>

<style scoped>
.main {
  max-width: 800px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.steps {
  margin-bottom: 1rem;
}

.section + .section {
  margin-top: 1rem;
}

.upload-block {
  margin-top: 0.5rem;
}

.hint {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #6b7280;
}

.footer-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}
</style>

