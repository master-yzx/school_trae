<template>
  <div class="campus-section">
    <div class="toolbar">
      <el-select
        v-model="innerSelectedCampusId"
        size="small"
        placeholder="选择校园"
        @change="handleCampusChange"
      >
        <el-option
          v-for="item in campuses"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
    </div>
    <div class="grid">
      <ProductCard
        v-for="item in goods"
        :key="item.id"
        :product="item"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import ProductCard from '../common/ProductCard.vue';

const props = defineProps({
  campuses: {
    type: Array,
    default: () => [],
  },
  goods: {
    type: Array,
    default: () => [],
  },
  selectedCampusId: {
    type: [String, Number, null],
    default: null,
  },
});

const emit = defineEmits(['update:selectedCampusId', 'change']);

const innerSelectedCampusId = ref(props.selectedCampusId);

watch(
  () => props.selectedCampusId,
  (val) => {
    innerSelectedCampusId.value = val;
  },
);

function handleCampusChange(val) {
  emit('update:selectedCampusId', val);
  emit('change', val);
}
</script>

<style scoped>
.campus-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 0.75rem 0.75rem 1rem;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 0.5rem;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1rem;
}
</style>

