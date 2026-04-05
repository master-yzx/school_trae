<template>
  <el-sub-menu v-if="hasChildren" :index="`cat-${node.id}`">
    <template #title>{{ node.name }}</template>

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

defineOptions({ name: 'CategoryMenuNode' });

const props = defineProps({
  node: {
    type: Object,
    required: true,
  },
});

const hasChildren = computed(() => !!(props.node?.children && props.node.children.length));
</script>

