<template>
  <AppLayout>
    <main class="home-main">
      <section class="section">
        <SectionTitle title="校园精选推荐" subtitle="热门/置顶好物，一站浏览" />
        <BannerCarousel :banners="bannerList" />
      </section>

      <section class="section">
        <SectionTitle title="为你推荐" subtitle="管理员精选 + 热门浏览" />
        <div class="product-grid">
          <ProductCard
            v-for="item in recommendList"
            :key="item.id"
            :product="item"
          />
        </div>
      </section>

      <section class="section">
        <SectionTitle title="校园专属好物" subtitle="按校园筛选，看看同校都在卖什么" />
        <CampusGoodsSection
          :campuses="campusList"
          :goods="campusGoods"
          :selected-campus-id="selectedCampusId"
          @update:selectedCampusId="setSelectedCampusId"
          @change="loadCampusGoods"
        />
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import AppLayout from '../../components/layout/AppLayout.vue';
import BannerCarousel from '../../components/common/BannerCarousel.vue';
import ProductCard from '../../components/common/ProductCard.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import CampusGoodsSection from '../../components/home/CampusGoodsSection.vue';
import request from '../../utils/request';
import { useAppStore } from '../../stores/app';

const bannerList = ref([]);
const recommendList = ref([]);
const campusGoods = ref([]);
const selectedCampusId = ref(null);

const appStore = useAppStore();
const campusList = computed(() => appStore.campuses);

function setSelectedCampusId(val) {
  selectedCampusId.value = val;
}

async function loadHomeData() {
  const safeGet = async (url) => {
    try {
      const res = await request.get(url);
      return res?.data;
    } catch (e) {
      return null;
    }
  };

  const [banners, recommend] = await Promise.all([
    safeGet('/banners'),
    safeGet('/products/recommend'),
  ]);

  const allBanners = banners || [];
  bannerList.value = allBanners.slice(0, 5);
  recommendList.value = recommend || [];

  if (campusList.value.length > 0) {
    selectedCampusId.value = campusList.value[0].id;
    await loadCampusGoods();
  }
}

async function loadCampusGoods() {
  if (!selectedCampusId.value) return;
  const res = await request.get(`/campuses/${selectedCampusId.value}/products`);
  campusGoods.value = res.data || [];
}

onMounted(async () => {
  await appStore.loadBaseData();
  loadHomeData();
});
</script>

<style scoped>
.home-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 2.5rem;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.25rem;
}
</style>

