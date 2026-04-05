<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="left">
        <span class="title">公告管理</span>
        <span class="sub">新增/编辑/删除公告</span>
      </div>
      <div class="right">
        <el-button type="primary" @click="openCreate">新增公告</el-button>
      </div>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable class="w-160" @change="loadData">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="活动" value="ACTIVITY" />
            <el-option label="规则" value="RULE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.enabled" placeholder="全部" clearable class="w-160" @change="loadData">
            <el-option label="启用" :value="true" />
            <el-option label="停用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" placeholder="标题/内容" clearable @keyup.enter="loadData" @clear="loadData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="260" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="scope">
            <el-tag effect="light">{{ typeLabel(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pinned" label="置顶" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.pinned ? 'danger' : 'info'" effect="light">
              {{ scope.row.pinned ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'warning'" effect="light">
              {{ scope.row.enabled ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-space :size="8" wrap>
              <el-button size="small" type="primary" plain @click="openEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" plain @click="removeOne(scope.row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          :total="total"
          :current-page="pageNum"
          :page-size="pageSize"
          @current-change="
            (p) => {
              pageNum = p;
              loadData();
            }
          "
          @size-change="
            (s) => {
              pageSize = s;
              pageNum = 1;
              loadData();
            }
          "
        />
      </div>
    </el-card>

    <el-dialog v-model="editVisible" :title="form.id ? '编辑公告' : '新增公告'" width="820px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="form.type" class="w-160">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="活动" value="ACTIVITY" />
            <el-option label="规则" value="RULE" />
          </el-select>
          <el-checkbox v-model="form.pinned" class="ml16">置顶</el-checkbox>
          <el-checkbox v-model="form.enabled" class="ml16">启用</el-checkbox>
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.content" type="textarea" :rows="10" />
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
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const query = ref({
  type: '',
  enabled: null,
  keyword: '',
});

const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);

const editVisible = ref(false);
const form = ref({
  id: null,
  title: '',
  type: 'SYSTEM',
  pinned: false,
  enabled: true,
  content: '',
});

function typeLabel(t) {
  if (t === 'ACTIVITY') return '活动';
  if (t === 'RULE') return '规则';
  return '系统通知';
}

async function loadData() {
  const res = await request.get('/admin/notices', {
    params: {
      type: query.value.type,
      enabled: query.value.enabled,
      keyword: query.value.keyword,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  });
  if (res.code === 0) {
    list.value = res.data.list || [];
    total.value = res.data.total || 0;
  }
}

function resetQuery() {
  query.value = { type: '', enabled: null, keyword: '' };
  pageNum.value = 1;
  loadData();
}

function openCreate() {
  form.value = { id: null, title: '', type: 'SYSTEM', pinned: false, enabled: true, content: '' };
  editVisible.value = true;
}

function openEdit(row) {
  form.value = {
    id: row.id,
    title: row.title || '',
    type: row.type || 'SYSTEM',
    pinned: !!row.pinned,
    enabled: row.enabled !== false,
    content: row.content || '',
  };
  editVisible.value = true;
}

async function save() {
  const payload = { ...form.value };
  const res = await request.post('/admin/notices', payload);
  if (res.code === 0) {
    ElMessage.success('已保存');
    editVisible.value = false;
    loadData();
  } else {
    ElMessage.warning(res.message || '保存失败');
  }
}

function removeOne(row) {
  ElMessageBox.confirm(`确认删除公告「${row.title}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/admin/notices/${row.id}`);
      ElMessage.success('已删除');
      loadData();
    })
    .catch(() => {});
}

onMounted(() => loadData());
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
.filter-card {
  margin-bottom: 16px;
}
.table-card {
  border-radius: 12px;
}
.pagination-bar {
  margin-top: 16px;
  text-align: right;
}
.w-160 {
  width: 160px;
}
.ml16 {
  margin-left: 16px;
}
</style>

