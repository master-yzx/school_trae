<template>

  <el-sub-menu v-if="hasChildren" :index="`cat-${node.id}`">
    <template #title>
      <span class="submenu-title-flex">
        <span>{{ node.name }}</span>
        <el-icon class="submenu-arrow"><ArrowRight /></el-icon>
      </span>
    </template>

    <!-- 允许选择父级分类：筛选出其所有子孙分类商品 -->
    <el-menu-item :index="`cat-${node.id}`">全部{{ node.name }}</el-menu-item>

    <CategoryMenuNode
      v-for="child in node.children"
      :key="child.id"
      :node="child"
    />
  </el-sub-menu>

  <el-menu-item v-else :index="`cat-${node.id}`">
    {{ node.name }}
  </el-menu-item>
</template>

<script setup>
import { computed } from 'vue';
import { ArrowRight } from '@element-plus/icons-vue';

defineOptions({ name: 'CategoryMenuNode' });

const props = defineProps({
  node: {
    type: Object,
    required: true,
  },
});

const hasChildren = computed(() => !!(props.node?.children && props.node.children.length));
</script>

<style scoped>
.submenu-title-flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 60px;
}
.submenu-arrow {
  margin-left: 10px;
  font-size: 1em;
  color: #6b7280;
  transition: color 0.2s;
}
.el-sub-menu.is-opened .submenu-arrow {
  color: #2563eb;
}
</style>

