<template>
  <div class="page">
    <section class="section">
      <div class="toolbar">
        <el-button type="primary" @click="openEdit(null)">新增卖家</el-button>
        <el-select
          v-model="status"
          placeholder="状态"
          class="w-140"
          clearable
          @change="loadList"
        >
          <el-option label="全部" :value="''" />
          <el-option label="正常" value="NORMAL" />
          <el-option label="禁用" value="DISABLED" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD"
          @change="loadList"
        />
        <el-input
          v-model="keyword"
          placeholder="昵称 / 学号 / 手机号"
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
        <el-button size="small" @click="batchEnable">批量启用</el-button>
        <el-button size="small" type="danger" plain @click="batchDisable">批量禁用</el-button>
      </div>
      <el-table
        :data="list"
        border
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="campusName" label="院校" width="120" />
        <el-table-column prop="productCount" label="商品数" width="90" />
        <el-table-column prop="registerTime" label="注册时间" width="160" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'" size="small">
              {{ row.status === 'NORMAL' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button text size="small" @click="view(row)">详情</el-button>
            <el-button text size="small" @click="openEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 'NORMAL'"
              text
              size="small"
              type="danger"
              @click="disable(row)"
            >禁用</el-button>
            <el-button
              v-else
              text
              size="small"
              type="success"
              @click="enable(row)"
            >启用</el-button>
            <el-button text size="small" @click="viewProducts(row)">查看商品</el-button>
            <el-button text size="small" type="danger" @click="remove(row)">删除</el-button>
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

    <el-dialog v-model="editVisible" :title="editForm.id ? '编辑卖家' : '新增卖家'" width="420px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="editForm.studentNo" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="院校">
          <el-input v-model="editForm.campusName" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const router = useRouter();

const status = ref('');
const keyword = ref('');
const dateRange = ref([]);
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 10;
const selectedIds = ref([]);

const editVisible = ref(false);
const editForm = ref({
  id: null,
  nickname: '',
  studentNo: '',
  phone: '',
  campusName: '',
});

async function loadList() {
  const [startTime, endTime] = dateRange.value || [];
  const res = await request.get('/admin/sellers', {
    params: {
      status: status.value,
      keyword: keyword.value,
      campusName: '',
      startTime,
      endTime,
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

function view(row) {
  openEdit(row);
}

function viewProducts(row) {
  // 简单跳到内容管控页，后续可带 sellerId 参数做筛选
  router.push({ name: 'AdminProductContent' });
}

function openEdit(row) {
  if (row) {
    editForm.value = { ...row };
  } else {
    editForm.value = {
      id: null,
      nickname: '',
      studentNo: '',
      phone: '',
      campusName: '',
    };
  }
  editVisible.value = true;
}

async function save() {
  await request.post('/admin/sellers/save', editForm.value);
  ElMessage.success('卖家已保存');
  editVisible.value = false;
  loadList();
}

async function disable(row) {
  await request.post(`/admin/sellers/${row.id}/disable`);
  ElMessage.success('卖家已禁用');
  loadList();
}

async function enable(row) {
  await request.post(`/admin/sellers/${row.id}/enable`);
  ElMessage.success('卖家已启用');
  loadList();
}

function remove(row) {
  ElMessageBox.confirm('确认删除该卖家吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/admin/sellers/${row.id}`);
      ElMessage.success('卖家已删除');
      loadList();
    })
    .catch(() => {});
}

function batchDisable() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择要禁用的卖家');
    return;
  }
  request.post('/admin/sellers/batch/disable', { ids: selectedIds.value }).then(() => {
    ElMessage.success('批量禁用已提交');
    loadList();
  });
}

function batchEnable() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择要启用的卖家');
    return;
  }
  request.post('/admin/sellers/batch/enable', { ids: selectedIds.value }).then(() => {
    ElMessage.success('批量启用已提交');
    loadList();
  });
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

.w-140 {
  width: 140px;
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
</style>

