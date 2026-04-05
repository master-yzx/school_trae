<template>
  <AppLayout>
    <main class="main">
      <section class="section">
        <el-button text type="primary" @click="goBack">← 返回</el-button>
      </section>

      <section class="section">
        <SectionTitle :title="title" subtitle="该卖家公开在售的闲置商品" />
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
            @current-change="(p) => { pageNum = p; loadList(); }"
          />
        </div>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import ProductCard from '../../components/common/ProductCard.vue';
import request from '../../utils/request';

const route = useRoute();
const router = useRouter();

const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 12;

const sellerId = computed(() => Number(route.params.id));
const title = computed(() => (Number.isFinite(sellerId.value) ? `卖家主页（ID：${sellerId.value}）` : '卖家主页'));

function goBack() {
  router.back();
}

async function loadList() {
  if (!Number.isFinite(sellerId.value)) return;
  const res = await request.get('/products/search', {
    params: {
      sellerId: sellerId.value,
      pageNum: pageNum.value,
      pageSize,
    },
  });
  const page = res.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
}

onMounted(() => {
  loadList();
});
</script>

<style scoped>
.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1rem;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1rem;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}
</style>

