<template>
  <div class="page">
    <section class="section">
      <div class="toolbar">
        <el-select
          v-model="status"
          placeholder="审核状态"
          class="w-160"
          clearable
          @change="loadList"
        >
          <el-option label="待审核" value="PENDING" />
          <el-option label="已驳回" value="REJECTED" />
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
      <el-table :data="list" border>
        <el-table-column label="标题" min-width="220">
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
        <el-table-column prop="submittedAt" label="提交时间" width="160" />
        <el-table-column prop="statusText" label="状态" width="90" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button text size="small" @click="viewDetail(row)">查看详情</el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              text
              type="success"
              size="small"
              @click="approve(row)"
            >通过</el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              text
              type="danger"
              size="small"
              @click="openReject(row)"
            >驳回</el-button>
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

    <el-dialog v-model="rejectDialogVisible" title="驳回原因" width="420px">
      <el-input
        v-model="rejectReason"
        type="textarea"
        rows="4"
        placeholder="请输入驳回原因"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';

const status = ref('PENDING');
const sellerKeyword = ref('');
const titleKeyword = ref('');

const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 10;

const rejectDialogVisible = ref(false);
const rejectReason = ref('');
const currentRow = ref(null);
const detailVisible = ref(false);
const detail = ref(null);

async function loadList() {
  const res = await request.get('/admin/review/products', {
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

async function approve(row) {
  await request.post(`/admin/review/products/${row.id}/approve`);
  ElMessage.success('审核通过');
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

function openReject(row) {
  currentRow.value = row;
  rejectReason.value = '';
  rejectDialogVisible.value = true;
}

async function confirmReject() {
  await request.post(`/admin/review/products/${currentRow.value.id}/reject`, {
    reason: rejectReason.value,
  });
  ElMessage.success('已驳回');
  rejectDialogVisible.value = false;
  loadList();
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
</style>

