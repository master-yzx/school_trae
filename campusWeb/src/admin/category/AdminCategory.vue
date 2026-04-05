<template>
  <div class="page">
    <section class="section">
      <div class="toolbar">
        <el-button type="primary" @click="openEdit(null)">新增分类</el-button>
        <el-button @click="saveSort">保存排序</el-button>
        <el-button @click="loadTree">刷新</el-button>
      </div>
    </section>

    <section class="section">
      <el-tree
        v-if="tree.length"
        v-model="expandedKeys"
        :data="tree"
        node-key="id"
        draggable
        default-expand-all
        :props="treeProps"
      >
        <template #default="{ data }">
          <div class="node">
            <span class="name">{{ data.name }}</span>
            <span class="status" :class="{ off: !data.enabled }">
              {{ data.enabled ? '显示' : '隐藏' }}
            </span>
            <div class="node-actions">
              <el-button text size="small" @click.stop="openEdit(data)">编辑</el-button>
              <el-button text size="small" @click.stop="toggle(data)">显示/隐藏</el-button>
              <el-button text type="danger" size="small" @click.stop="remove(data)">删除</el-button>
            </div>
          </div>
        </template>
      </el-tree>
    </section>

    <el-dialog v-model="editVisible" :title="editForm.id ? '编辑分类' : '新增分类'" width="420px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="父级分类">
          <el-select v-model="editForm.parentId" placeholder="顶级分类">
            <el-option :value="0" label="顶级分类" />
            <el-option
              v-for="item in flatList"
              :key="item.id"
              :value="item.id"
              :label="item.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="显示">
          <el-switch v-model="editForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../../utils/request';

const tree = ref([]);
const expandedKeys = ref([]);
const treeProps = { children: 'children', label: 'name' };

const editVisible = ref(false);
const editForm = ref({
  id: null,
  parentId: 0,
  name: '',
  enabled: true,
});

const flatList = computed(() => {
  const res = [];
  function dfs(nodes, prefix = '') {
    nodes.forEach((n) => {
      res.push({ id: n.id, label: `${prefix}${n.name}` });
      if (n.children && n.children.length) {
        dfs(n.children, `${prefix}${n.name} / `);
      }
    });
  }
  dfs(tree.value);
  return res;
});

async function loadTree() {
  const res = await request.get('/admin/categories/tree');
  tree.value = res.data || [];
}

function openEdit(node) {
  if (node) {
    editForm.value = {
      id: node.id,
      parentId: node.parentId ?? 0,
      name: node.name,
      enabled: node.enabled,
    };
  } else {
    editForm.value = {
      id: null,
      parentId: 0,
      name: '',
      enabled: true,
    };
  }
  editVisible.value = true;
}

async function saveCategory() {
  await request.post('/admin/categories/save', editForm.value);
  ElMessage.success('分类已保存');
  editVisible.value = false;
  loadTree();
}

async function toggle(node) {
  await request.post(`/admin/categories/${node.id}/toggle`);
  ElMessage.success('状态已切换');
  loadTree();
}

function remove(node) {
  ElMessageBox.confirm(`确认删除分类「${node.name}」及其子分类吗？`, '提示', {
    type: 'warning',
  })
    .then(async () => {
      await request.delete(`/admin/categories/${node.id}`);
      ElMessage.success('分类已删除');
      loadTree();
    })
    .catch(() => {});
}

async function saveSort() {
  await request.post('/admin/categories/sort', tree.value);
  ElMessage.success('排序已保存');
}

onMounted(() => {
  loadTree();
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.toolbar {
  display: flex;
  gap: 0.75rem;
}

.node {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
}

.name {
  font-size: 0.95rem;
}

.status {
  font-size: 0.8rem;
  padding: 2px 8px;
  border-radius: 999px;
  background: #ecfdf5;
  color: #16a34a;
}

.status.off {
  background: #fee2e2;
  color: #b91c1c;
}

.node-actions {
  margin-left: auto;
  display: flex;
  gap: 0.25rem;
}
</style>

