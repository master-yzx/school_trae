<template>
  <AppLayout>
    <main class="main">
      <section class="section">
        <SectionTitle title="全部商品" subtitle="支持按关键词、分类、价格、校园等多维度筛选" />

        <div class="top-bar">
          <el-input
            v-model="query.keyword"
            placeholder="输入关键词搜索商品"
            class="keyword-input"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>

          <el-select
            v-model="query.categoryId"
            placeholder="分类"
            clearable
            class="w-160"
            @change="onCategoryChange"
          >
            <el-option
              v-for="item in flatCategories"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </el-select>

          <el-select
            v-model="query.campusId"
            placeholder="校园"
            clearable
            class="w-140"
            @change="onCampusChange"
          >
            <el-option
              v-for="item in campusList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>

          <el-button @click="resetFilters">清空条件</el-button>
        </div>

        <div class="filter-bar">
          <div class="group">
            <span class="label">价格：</span>
            <el-input-number
              v-model="minPrice"
              :min="0"
              :max="maxPrice || 99999"
              @change="onPriceChange"
              size="small"
              controls-position="right"
            />
            <span class="sep">-</span>
            <el-input-number
              v-model="maxPrice"
              :min="0"
              :max="99999"
              @change="onPriceChange"
              size="small"
              controls-position="right"
            />
          </div>

          <div class="group">
            <span class="label">新旧：</span>
            <el-radio-group v-model="query.condition" size="small" @change="onConditionChange">
              <el-radio-button label="">不限</el-radio-button>
              <el-radio-button label="九成新">九成新</el-radio-button>
              <el-radio-button label="八成新">八成新</el-radio-button>
            </el-radio-group>
          </div>

          <div class="group">
            <span class="label">交易：</span>
            <el-radio-group v-model="query.deliveryType" size="small" @change="onDeliveryTypeChange">
              <el-radio-button label="">不限</el-radio-button>
              <el-radio-button label="自提">自提</el-radio-button>
              <el-radio-button label="邮寄">邮寄</el-radio-button>
            </el-radio-group>
          </div>

          <div class="group">
            <span class="label">包邮：</span>
            <el-radio-group v-model="query.freeShipping" size="small" @change="onFreeShippingChange">
              <el-radio-button :label="null">不限</el-radio-button>
              <el-radio-button :label="true">仅看包邮</el-radio-button>
            </el-radio-group>
          </div>

          <div class="group">
            <span class="label">排序：</span>
            <el-radio-group v-model="sortUi" size="small" @change="onSortChange">
              <el-radio-button label="TIME_DESC">发布时间</el-radio-button>
              <el-radio-button label="PRICE_ASC">价格↑</el-radio-button>
              <el-radio-button label="PRICE_DESC">价格↓</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </section>

      <section class="section content">
        <aside class="sidebar">
          <SectionTitle title="已选条件" />
          <div v-if="selectedTags.length" class="tags">
            <el-tag
              v-for="tag in selectedTags"
              :key="tag.key"
              closable
              @close="removeTag(tag.key)"
            >
              {{ tag.label }}
            </el-tag>
          </div>
          <div v-else class="empty-tags">暂无筛选条件</div>
        </aside>

        <div class="list-area">
          <div class="product-grid">
            <ProductCard
              v-for="item in list"
              :key="item.id"
              :product="item"
            />
          </div>

          <div class="pagination">
            <el-pagination
              background
              layout="total, sizes, prev, pager, next"
              :total="total"
              :current-page="query.pageNum"
              :page-size="query.pageSize"
              :page-sizes="[12, 24, 48]"
              @current-change="(p) => { query.pageNum = p; handleSearch(); }"
              @size-change="(s) => { query.pageSize = s; query.pageNum = 1; handleSearch(); }"
            />
          </div>
        </div>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import ProductCard from '../../components/common/ProductCard.vue';
import request from '../../utils/request';
import { useAppStore } from '../../stores/app';

const route = useRoute();
const router = useRouter();

const appStore = useAppStore();
const categoryTree = computed(() => appStore.categories);
const campusList = computed(() => appStore.campuses);
const flatCategories = computed(() => flattenCategories(categoryTree.value || []));

const list = ref([]);
const total = ref(0);

const query = reactive({
  keyword: '',
  categoryId: null,
  campusId: null,
  condition: '',
  deliveryType: '',
  freeShipping: null,
  sortField: 'createdAt',
  sortOrder: 'desc',
  pageNum: 1,
  pageSize: 12,
});

const minPrice = ref(null);
const maxPrice = ref(null);
const sortUi = ref('TIME_DESC');

const selectedTags = computed(() => {
  const tags = [];
  if (query.keyword) tags.push({ key: 'keyword', label: `关键词：${query.keyword}` });
  if (query.categoryId) {
    const cat = flatCategories.value.find((c) => c.id === query.categoryId);
    if (cat) tags.push({ key: 'categoryId', label: `分类：${cat.label}` });
  }
  if (query.campusId) {
    const c = campusList.value.find((x) => x.id === query.campusId);
    if (c) tags.push({ key: 'campusId', label: `校园：${c.name}` });
  }
  if (minPrice.value != null || maxPrice.value != null) {
    tags.push({ key: 'price', label: `价格：${minPrice.value || 0} - ${maxPrice.value || '不限'}` });
  }
  if (query.condition) tags.push({ key: 'condition', label: '新旧：指定' });
  if (query.deliveryType) tags.push({ key: 'deliveryType', label: '交易方式：指定' });
  if (query.freeShipping === true) tags.push({ key: 'freeShipping', label: '仅看包邮' });
  return tags;
});

function removeTag(key) {
  switch (key) {
    case 'keyword':
      query.keyword = '';
      break;
    case 'categoryId':
      query.categoryId = null;
      break;
    case 'campusId':
      query.campusId = null;
      break;
    case 'price':
      minPrice.value = null;
      maxPrice.value = null;
      break;
    case 'condition':
      query.condition = '';
      break;
    case 'deliveryType':
      query.deliveryType = '';
      break;
    case 'freeShipping':
      query.freeShipping = null;
      break;
    default:
      break;
  }
  query.pageNum = 1;
  handleSearch();
}

function applySortUi() {
  switch (sortUi.value) {
    case 'PRICE_ASC':
      query.sortField = 'price';
      query.sortOrder = 'asc';
      break;
    case 'PRICE_DESC':
      query.sortField = 'price';
      query.sortOrder = 'desc';
      break;
    default:
      query.sortField = 'createdAt';
      query.sortOrder = 'desc';
      break;
  }
}

function flattenCategories(tree, prefix = '') {
  const result = [];
  const dfs = (nodes, currentPrefix) => {
    nodes.forEach((n) => {
      const label = currentPrefix ? `${currentPrefix} / ${n.name}` : n.name;
      // 所有层级都可作为选项
      result.push({ id: n.id, label });
      if (n.children && n.children.length) {
        dfs(n.children, label);
      }
    });
  };
  dfs(tree || [], prefix);
  return result;
}

function applyRouteQuery() {
  const q = route.query || {};
  if (typeof q.keyword === 'string') query.keyword = q.keyword;
  if (q.categoryId != null) {
    const v = Number(q.categoryId);
    query.categoryId = Number.isFinite(v) ? v : null;
  }
  if (q.campusId != null) {
    const v = Number(q.campusId);
    query.campusId = Number.isFinite(v) ? v : null;
  }
}

function syncRouteQuery() {
  router.replace({
    name: 'ProductList',
    query: {
      keyword: query.keyword || undefined,
      categoryId: query.categoryId || undefined,
      campusId: query.campusId || undefined,
    },
  });
}

async function searchInternal(syncRoute) {
  if (syncRoute) {
    syncRouteQuery();
  }
  const rawParams = {
    keyword: query.keyword,
    categoryId: query.categoryId,
    campusId: query.campusId,
    minPrice: minPrice.value,
    maxPrice: maxPrice.value,
    condition: query.condition,
    deliveryType: query.deliveryType,
    freeShipping: query.freeShipping,
    sortField: query.sortField,
    sortOrder: query.sortOrder,
    pageNum: query.pageNum,
    pageSize: query.pageSize,
  };

  // 过滤掉后端不需要或可能造成类型歧义的空值
  const params = {};
  Object.entries(rawParams).forEach(([key, value]) => {
    if (value === null || value === undefined) return;
    if (typeof value === 'string' && value.trim() === '') return;
    params[key] = value;
  });

  try {
    const res = await request.get('/products/search', { params });
    const page = res.data || {};
    list.value = page.list || [];
    total.value = page.total || 0;
  } catch (e) {
    // 出错时提示一条消息，避免页面完全卡死在旧数据
    ElMessage.error(e?.response?.data?.message || '商品列表加载失败，请稍后重试');
  }
}

async function handleSearch() {
  await searchInternal(true);
}

function resetFilters() {
  query.keyword = '';
  query.categoryId = null;
  query.campusId = null;
  query.condition = '';
  query.deliveryType = '';
  query.freeShipping = null;
  sortUi.value = 'TIME_DESC';
  applySortUi();
  minPrice.value = null;
  maxPrice.value = null;
  query.pageNum = 1;
  handleSearch();
}

function onCategoryChange() {
  query.pageNum = 1;
  handleSearch();
}

function onCampusChange() {
  query.pageNum = 1;
  handleSearch();
}

function onConditionChange() {
  query.pageNum = 1;
  handleSearch();
}

function onDeliveryTypeChange() {
  query.pageNum = 1;
  handleSearch();
}

function onFreeShippingChange() {
  query.pageNum = 1;
  handleSearch();
}

function onPriceChange() {
  query.pageNum = 1;
  handleSearch();
}

function onSortChange() {
  applySortUi();
  query.pageNum = 1;
  handleSearch();
}

onMounted(async () => {
  await appStore.loadBaseData();
  applyRouteQuery();
  applySortUi();
  await searchInternal(false);
});

watch(
  () => route.query,
  () => {
    applyRouteQuery();
    query.pageNum = 1;
    searchInternal(false);
  },
);
</script>

<style scoped>
.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1.5rem;
}

.top-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
}

.keyword-input {
  flex: 1 1 260px;
}

.w-160 {
  width: 160px;
}

.w-140 {
  width: 140px;
}

.filter-bar {
  margin-top: 1rem;
  padding: 0.75rem;
  border-radius: 10px;
  background: #ffffff;
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem 1.5rem;
  align-items: center;
}

.group {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.group .label {
  font-size: 0.85rem;
  color: #4b5563;
}

.sep {
  margin: 0 4px;
}

.content {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  gap: 1rem;
}

.sidebar {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.75rem;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.empty-tags {
  font-size: 0.85rem;
  color: #9ca3af;
}

.list-area {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.75rem 0.75rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1rem;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>

