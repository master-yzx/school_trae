<template>
  <div class="page">
    <section class="section">
      <div class="toolbar">
        <el-select
          v-model="status"
          placeholder="商品状态"
          class="w-160"
          clearable
          @change="loadList"
        >
          <el-option label="全部" :value="''" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="下架" value="OFFLINE" />
        </el-select>

        <el-input
          v-model="sellerKeyword"
          placeholder="卖家昵称"
          class="w-180"
          clearable
          @keyup.enter="loadList"
          @clear="loadList"
        />

        <el-input
          v-model="titleKeyword"
          placeholder="商品标题"
          class="keyword-input"
          clearable
          @keyup.enter="loadList"
          @clear="loadList"
        >
          <template #append>
            <el-button @click="loadList">搜索</el-button>
          </template>
        </el-input>

        <el-button @click="loadList">刷新</el-button>
      </div>
    </section>

    <section class="section">
      <div class="batch-actions">
        <el-button size="small" @click="batchOffline">批量下架</el-button>
        <el-button size="small" type="danger" plain @click="batchDelete">批量删除</el-button>
      </div>
      <el-table
        :data="list"
        border
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" />
        <el-table-column label="商品" min-width="260">
          <template #default="{ row }">
            <div class="title-cell">
              <img :src="row.coverUrl" alt="" />
              <div>
                <div class="title">{{ row.title }}</div>
                <div class="sub">{{ row.sellerName }} · {{ row.campusName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="80">
          <template #default="{ row }">
            ￥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="90" />
        <el-table-column prop="createdAt" label="发布时间" width="160" />
        <el-table-column label="操作" width="320">
          <template #default="{ row }">
            <el-button text size="small" @click="viewDetail(row)">查看详情</el-button>
            <el-button text size="small" @click="openAudit(row)">审核</el-button>
            <el-button text size="small" type="warning" @click="offline(row)">下架</el-button>
            <el-button text size="small" type="danger" @click="remove(row)">删除</el-button>
            <el-button
              v-if="row.rejectReason"
              text
              size="small"
              @click="showReason(row)"
            >查看驳回原因</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          v-model:current-page="pageNum"
          :page-size="pageSize"
          @current-change="loadList"
        />
      </div>
    </section>

    <el-dialog v-model="auditDialogVisible" title="审核商品" width="420px">
      <el-radio-group v-model="auditForm.pass">
        <el-radio :label="true">通过</el-radio>
        <el-radio :label="false">驳回</el-radio>
      </el-radio-group>
      <el-input
        v-if="auditForm.pass === false"
        v-model="auditForm.reason"
        type="textarea"
        rows="4"
        class="mt8"
        placeholder="请输入驳回原因"
      />
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reasonDialogVisible" title="驳回原因" width="420px">
      <p>{{ currentReason }}</p>
      <template #footer>
        <el-button @click="reasonDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="商品详情" width="720px">
      <div v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="标题" :span="2">{{ detail.title }}</el-descriptions-item>
          <el-descriptions-item label="价格">￥{{ detail.price }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detail.statusText || detail.status }}</el-descriptions-item>
          <el-descriptions-item label="卖家" :span="2">{{ detail.sellerName }}</el-descriptions-item>
        </el-descriptions>
        <div style="margin-top: 12px" v-if="detail.mediaList && detail.mediaList.length">
          <el-image
            v-for="m in detail.mediaList"
            :key="m.url"
            :src="m.url"
            style="width: 120px; height: 120px; margin-right: 8px; border-radius: 8px"
            fit="cover"
          />
        </div>
        <div style="margin-top: 12px; white-space: pre-wrap" v-if="detail.descriptionHtml">
          {{ detail.descriptionHtml }}
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const status = ref('');
const sellerKeyword = ref('');
const titleKeyword = ref('');
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 10;
const selectedIds = ref([]);

const auditDialogVisible = ref(false);
const auditForm = ref({
  id: null,
  pass: true,
  reason: '',
});

const reasonDialogVisible = ref(false);
const currentReason = ref('');
const detailVisible = ref(false);
const detail = ref(null);

async function loadList() {
  const res = await request.get('/admin/content/products', {
    params: {
      status: status.value,
      sellerKeyword: sellerKeyword.value,
      titleKeyword: titleKeyword.value,
      pageNum: pageNum.value,
      pageSize,
    },
  });
  const page = res.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
}

function onSelectionChange(rows) {
  selectedIds.value = rows.map((r) => r.id);
}

function openAudit(row) {
  auditForm.value.id = row.id;
  auditForm.value.pass = true;
  auditForm.value.reason = '';
  auditDialogVisible.value = true;
}

async function submitAudit() {
  await request.post(`/admin/content/products/${auditForm.value.id}/audit`, {
    pass: auditForm.value.pass,
    reason: auditForm.value.reason,
  });
  ElMessage.success('审核操作已提交');
  auditDialogVisible.value = false;
  loadList();
}

async function viewDetail(row) {
  const res = await request.get(`/products/${row.id}`);
  if (res.code === 0) {
    detail.value = res.data || null;
    detailVisible.value = true;
  } else {
    ElMessage.warning(res.message || '获取详情失败');
  }
}

async function offline(row) {
  await request.post(`/admin/content/products/${row.id}/offline`);
  ElMessage.success('已下架');
  loadList();
}

function remove(row) {
  ElMessageBox.confirm('确认删除该商品吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/admin/content/products/${row.id}`);
      ElMessage.success('已删除');
      loadList();
    })
    .catch(() => {});
}

function batchOffline() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择要下架的商品');
    return;
  }
  ElMessageBox.confirm('确认批量下架选中的商品吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.post('/admin/content/products/batch/offline', { ids: selectedIds.value });
        ElMessage.success('批量下架已提交');
      loadList();
    })
    .catch(() => {});
}

function batchDelete() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择要删除的商品');
    return;
  }
  ElMessageBox.confirm('确认批量删除选中的商品吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.post('/admin/content/products/batch/delete', { ids: selectedIds.value });
        ElMessage.success('批量删除已提交');
      loadList();
    })
    .catch(() => {});
}

async function showReason(row) {
  if (row.rejectReason) {
    currentReason.value = row.rejectReason;
  } else {
    const res = await request.get(`/admin/content/products/${row.id}/reject-reason`);
    currentReason.value = res.data || '';
  }
  reasonDialogVisible.value = true;
}

onMounted(() => {
  loadList();
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.section + .section {
  margin-top: 0.5rem;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
}

.w-160 {
  width: 160px;
}

.w-180 {
  width: 180px;
}

.keyword-input {
  flex: 1 1 260px;
}

.batch-actions {
  margin-bottom: 0.5rem;
  display: flex;
  gap: 0.5rem;
}

.pagination {
  margin-top: 0.75rem;
  display: flex;
  justify-content: flex-end;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.title-cell img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 8px;
}

.title {
  font-size: 0.95rem;
  font-weight: 500;
}

.sub {
  font-size: 0.8rem;
  color: #6b7280;
}

.mt8 {
  margin-top: 8px;
}
</style>

