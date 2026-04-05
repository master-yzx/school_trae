<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="我的收藏" />

      <section class="section">
        <div class="top-bar">
          <el-select
            v-model="currentCategoryId"
            placeholder="收藏分类"
            class="w-180"
            clearable
          >
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>

          <el-input
            v-model="keyword"
            placeholder="搜索收藏的商品"
            class="keyword-input"
            clearable
            @keyup.enter="loadList"
            @clear="loadList"
          >
            <template #append>
              <el-button @click="loadList">搜索</el-button>
            </template>
          </el-input>

          <el-button @click="clearAll" type="danger" plain>清空全部收藏</el-button>
        </div>
      </section>

      <section class="section content">
        <aside class="sidebar">
          <SectionTitle title="收藏分类" />
          <div class="cats">
            <el-tag
              v-for="cat in categories"
              :key="cat.id"
              :type="currentCategoryId === cat.id ? 'primary' : 'info'"
              effect="plain"
              class="cat-tag"
              @click="selectCategory(cat.id)"
            >
              {{ cat.name }}
            </el-tag>
          </div>
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
              layout="total, prev, pager, next"
              :total="total"
              :current-page="pageNum"
              :page-size="pageSize"
              @current-change="
                (p) => {
                  pageNum = p;
                  loadList();
                }
              "
            />
          </div>
        </div>
      </section>

      <section class="section">
        <el-button @click="goBack">返回个人中心</el-button>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import ProductCard from '../../components/common/ProductCard.vue';
import request from '../../utils/request';

const router = useRouter();

const categories = ref([]);
const currentCategoryId = ref(null);
const keyword = ref('');

const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 12;

function selectCategory(id) {
  currentCategoryId.value = id;
  pageNum.value = 1;
  loadList();
}

async function loadCategories() {
  const res = await request.get('/favorite/categories');
  categories.value = res.data || [];
}

async function loadList() {
  const res = await request.get('/favorite/list', {
    params: {
      categoryId: currentCategoryId.value,
      keyword: keyword.value,
      pageNum: pageNum.value,
      pageSize,
    },
  });
  const page = res.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
}

function clearAll() {
  ElMessageBox.confirm('确认清空全部收藏吗？', '提示', {
    type: 'warning',
  })
    .then(async () => {
      try {
        await request.delete('/favorite/clear');
        ElMessage.success('已清空收藏');
        loadList();
      } catch (e) {
        ElMessage.warning(e?.response?.data?.message || '清空失败，请先登录');
      }
    })
    .catch(() => {});
}

function goBack() {
  router.push({ name: 'ProfileHome' });
}

onMounted(async () => {
  await loadCategories();
  await loadList();
});
</script>

<style scoped>
.main {
  max-width: 1040px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1rem;
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

.w-180 {
  width: 180px;
}

.content {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 1rem;
}

.sidebar {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.75rem;
}

.cats {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.cat-tag {
  cursor: pointer;
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

