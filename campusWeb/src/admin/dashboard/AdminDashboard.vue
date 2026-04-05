<template>
  <div class="page">
    <section class="header-row">
      <div>
        <h2 class="page-title">数据概览</h2>
        <p class="page-subtitle">快速了解平台当前核心运营数据</p>
      </div>
      <el-button class="refresh" size="small" @click="loadCards">刷新数据</el-button>
    </section>

    <section class="cards">
      <el-card
        v-for="card in cards"
        :key="card.key"
        class="card"
      >
        <div class="card-title">{{ card.label }}</div>
        <div class="card-value">{{ card.value }}</div>
      </el-card>
    </section>

    <section class="charts">
      <el-card>
        <div class="chart-header">
          <span class="chart-title">最近 7 天订单趋势</span>
          <span class="chart-tip">订单数量（按天统计）</span>
        </div>
        <div ref="orderChartRef" class="chart"></div>
      </el-card>
    </section>

    <section class="charts two-cols">
      <el-card>
        <div class="chart-header">
          <span class="chart-title">最近 7 天用户增长</span>
          <span class="chart-tip">注册用户数（按天统计）</span>
        </div>
        <div ref="userChartRef" class="chart small"></div>
      </el-card>
      <el-card>
        <div class="chart-header">
          <span class="chart-title">分类占比</span>
          <span class="chart-tip">按商品数量统计</span>
        </div>
        <div ref="categoryChartRef" class="chart small"></div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue';
import request from '../../utils/request';
import * as echarts from 'echarts';

const cards = ref([]);
const orderTrend = ref([]);
const userGrowth = ref([]);
const categoryDist = ref([]);

const orderChartRef = ref();
const userChartRef = ref();
const categoryChartRef = ref();

let orderChart;
let userChart;
let categoryChart;

async function loadCards() {
  const [cardsRes, orderRes, userRes, catRes] = await Promise.all([
    request.get('/admin/stats/overview'),
    request.get('/admin/stats/orders'),
    request.get('/admin/stats/users'),
    request.get('/admin/stats/categories'),
  ]);
  const overview = cardsRes.data || {};
  cards.value = [
    { key: 'todayOrders', label: '今日订单数', value: overview.todayOrderCount ?? 0 },
    { key: 'sevenDayOrders', label: '近7日订单', value: overview.sevenDayOrderCount ?? 0 },
    { key: 'totalUsers', label: '用户总数', value: overview.totalUserCount ?? 0 },
    { key: 'totalSellers', label: '卖家总数', value: overview.totalSellerCount ?? 0 },
    { key: 'totalProducts', label: '商品总数', value: overview.totalProductCount ?? 0 },
  ];
  orderTrend.value = orderRes.data || [];
  userGrowth.value = userRes.data || [];
  categoryDist.value = catRes.data || [];
  renderCharts();
}

function renderCharts() {
  renderOrderChart();
  renderUserChart();
  renderCategoryChart();
}

function renderOrderChart() {
  if (!orderChartRef.value) return;
  if (!orderChart) {
    orderChart = echarts.init(orderChartRef.value);
  }
  const labels = orderTrend.value.map((p) => p.label);
  const values = orderTrend.value.map((p) => p.value);
  orderChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: labels, boundaryGap: false },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        areaStyle: {},
        data: values,
      },
    ],
  });
}

function renderUserChart() {
  if (!userChartRef.value) return;
  if (!userChart) {
    userChart = echarts.init(userChartRef.value);
  }
  const labels = userGrowth.value.map((p) => p.label);
  const values = userGrowth.value.map((p) => p.value);
  userChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: labels, boundaryGap: false },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        data: values,
      },
    ],
  });
}

function renderCategoryChart() {
  if (!categoryChartRef.value) return;
  if (!categoryChart) {
    categoryChart = echarts.init(categoryChartRef.value);
  }
  const data = categoryDist.value.map((p) => ({ name: p.label, value: p.value }));
  categoryChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        name: '分类占比',
        type: 'pie',
        radius: ['40%', '65%'],
        avoidLabelOverlap: false,
        data,
      },
    ],
  });
}

onMounted(() => {
  loadCards();
  window.addEventListener('resize', handleResize);
});

function handleResize() {
  orderChart && orderChart.resize();
  userChart && userChart.resize();
  categoryChart && categoryChart.resize();
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  orderChart && orderChart.dispose();
  userChart && userChart.dispose();
  categoryChart && categoryChart.dispose();
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  margin: 0 0 4px;
  font-size: 1.25rem;
  font-weight: 700;
  color: #111827;
}

.page-subtitle {
  margin: 0;
  font-size: 0.9rem;
  color: #6b7280;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 0.75rem;
  align-items: stretch;
}

.card {
  position: relative;
  border-radius: 14px;
}

.card-title {
  font-size: 0.9rem;
  color: #6b7280;
}

.card-value {
  margin-top: 0.4rem;
  font-size: 1.4rem;
  font-weight: 600;
}

.refresh {
  align-self: center;
}

.charts .chart-placeholder {
  height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  border-radius: 12px;
  border: 1px dashed #d1d5db;
  background: #f9fafb;
}

.charts {
  margin-top: 0.5rem;
}

.charts.two-cols {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 0.75rem;
}

.chart {
  width: 100%;
  height: 260px;
}

.chart.small {
  height: 220px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.chart-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #111827;
}

.chart-tip {
  font-size: 0.8rem;
  color: #9ca3af;
}
</style>

