<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="left">
        <span class="title">管理员管理</span>
        <span class="sub">仅超级管理员可见</span>
      </div>
      <div class="right">
        <el-button type="primary" @click="openEdit()">新增管理员</el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="roleName" label="角色" width="120" />
        <el-table-column prop="phone" label="联系方式" width="150" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="权限" min-width="220">
          <template #default="scope">
            <el-tag
              v-for="p in scope.row.permissions || []"
              :key="p"
              size="small"
              style="margin-right: 4px; margin-bottom: 2px"
            >
              {{ permissionLabel(p) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" link @click="openPermission(scope.row)">分配权限</el-button>
            <el-button size="small" type="danger" link @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20]"
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          @change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="editVisible" :title="editForm.id ? '编辑管理员' : '新增管理员'" width="460px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="密码" v-if="!editForm.id">
          <el-input v-model="editForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.roleName" placeholder="请选择角色">
            <el-option label="超级管理员" value="超级管理员" />
            <el-option label="普通管理员" value="普通管理员" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveAdmin">保 存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permVisible" title="分配权限" width="420px">
      <el-checkbox-group v-model="permForm.permissions">
        <el-checkbox v-for="p in allPermissions" :key="p.value" :label="p.value">
          {{ p.label }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="permVisible = false">取 消</el-button>
        <el-button type="primary" @click="savePermissions">保 存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);

const editVisible = ref(false);
const editForm = ref({
  id: null,
  username: '',
  password: '',
  phone: '',
  roleName: '普通管理员',
});

const permVisible = ref(false);
const permForm = ref({
  id: null,
  permissions: [],
});

const allPermissions = [
  { value: 'dashboard', label: '数据看板' },
  { value: 'order', label: '订单管理' },
  { value: 'user', label: '用户管理' },
  { value: 'seller', label: '卖家管理' },
  { value: 'category', label: '分类管理' },
  { value: 'content', label: '商品内容管理' },
  { value: 'settings', label: '系统配置' },
  { value: 'log', label: '操作日志' },
];

const permissionLabel = (value) => {
  const item = allPermissions.find((p) => p.value === value);
  return item ? item.label : value;
};

const loadData = async () => {
  const res = await request.get('/admin/managers', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  });
  if (res.code === 0) {
    list.value = res.data.list || [];
    total.value = res.data.total || 0;
  }
};

const openEdit = (row) => {
  if (row) {
    editForm.value = {
      id: row.id,
      username: row.username,
      password: '',
      phone: row.phone,
      roleName: row.roleName,
    };
  } else {
    editForm.value = {
      id: null,
      username: '',
      password: '',
      phone: '',
      roleName: '普通管理员',
    };
  }
  editVisible.value = true;
};

const saveAdmin = async () => {
  if (!editForm.value.username) {
    ElMessage.warning('请输入用户名');
    return;
  }

  // 新增管理员时，先检查是否已有同名用户，再决定是否升级为管理员
  if (!editForm.value.id) {
    const checkRes = await request.get('/admin/managers/check-user', {
      params: { username: editForm.value.username },
    });
    if (checkRes.code === 0 && checkRes.data === true) {
      try {
        await ElMessageBox.confirm(
          '系统中已存在该用户，是否将该用户设置为管理员并赋予对应权限？',
          '提示',
          { type: 'warning' },
        );
      } catch {
        // 选择取消
        return;
      }
    }
  }

  await request.post('/admin/managers', editForm.value);
  ElMessage.success('已保存管理员信息');
  editVisible.value = false;
  loadData();
};

const remove = (row) => {
  ElMessageBox.confirm(`确定删除管理员【${row.username}】吗？`, '提示', {
    type: 'warning',
  }).then(async () => {
    await request.delete(`/admin/managers/${row.id}`);
    ElMessage.success('已删除管理员');
    loadData();
  }).catch(() => {});
};

const openPermission = (row) => {
  permForm.value = {
    id: row.id,
    permissions: [...(row.permissions || [])],
  };
  permVisible.value = true;
};

const savePermissions = async () => {
  await request.post(`/admin/managers/${permForm.value.id}/permissions`, permForm.value.permissions);
  ElMessage.success('已保存权限配置');
  permVisible.value = false;
  loadData();
};

const goBack = () => {
  window.location.href = '/admin/dashboard';
};

onMounted(() => {
  loadData();
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
.pagination-bar {
  margin-top: 16px;
  text-align: right;
}
</style>

