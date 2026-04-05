<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="left">
        <span class="title">数据统计分析</span>
        <span class="sub">平台关键指标</span>
      </div>
      <div class="right">
        <el-button type="primary" @click="goBack">返回后台首页</el-button>
      </div>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="时间范围">
          <el-select v-model="query.quickRange" placeholder="请选择">
            <el-option label="今日" value="today" />
            <el-option label="近7天" value="7d" />
            <el-option label="近30天" value="30d" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="query.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadAll">筛选</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button @click="loadAll">刷新</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="exportReport">导出报表</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" class="overview-row">
      <el-col :span="5">
        <el-card shadow="never">
          <div class="overview-item">
            <div class="label">今日订单数</div>
            <div class="value">{{ overview.todayOrderCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card shadow="never">
          <div class="overview-item">
            <div class="label">近7日订单</div>
            <div class="value">{{ overview.sevenDayOrderCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="never">
          <div class="overview-item">
            <div class="label">用户总数</div>
            <div class="value">{{ overview.totalUserCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="never">
          <div class="overview-item">
            <div class="label">卖家总数</div>
            <div class="value">{{ overview.totalSellerCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="overview-item">
            <div class="label">商品总数</div>
            <div class="value">{{ overview.totalProductCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>订单趋势</template>
          <div ref="orderChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>用户增长</template>
          <div ref="userChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>分类占比</template>
          <div ref="categoryChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>明细数据</template>
          <el-table :data="detailList" height="260">
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="value" label="数值" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import request from '../../utils/request';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';

const router = useRouter();

const query = ref({
  quickRange: '7d',
  dateRange: [],
});

const overview = ref({
  todayOrderCount: 0,
  sevenDayOrderCount: 0,
  totalUserCount: 0,
  totalSellerCount: 0,
  totalProductCount: 0,
});

const orderTrend = ref([]);
const userGrowth = ref([]);
const categoryDistribution = ref([]);
const detailList = ref([]);

const orderChartRef = ref();
const userChartRef = ref();
const categoryChartRef = ref();

let orderChart;
let userChart;
let categoryChart;

const buildTimeParams = () => {
  const params = {};
  if (query.value.dateRange && query.value.dateRange.length === 2) {
    params.startTime = query.value.dateRange[0];
    params.endTime = query.value.dateRange[1];
  }
  return params;
};

const loadOverview = async () => {
  const res = await request.get('/admin/stats/overview', { params: buildTimeParams() });
  if (res.code === 0) {
    overview.value = res.data || overview.value;
  }
};

const loadOrderTrend = async () => {
  const res = await request.get('/admin/stats/orders', { params: buildTimeParams() });
  if (res.code === 0) {
    orderTrend.value = res.data || [];
    renderOrderChart();
  }
};

const loadUserGrowth = async () => {
  const res = await request.get('/admin/stats/users', { params: buildTimeParams() });
  if (res.code === 0) {
    userGrowth.value = res.data || [];
    renderUserChart();
  }
};

const loadCategoryDistribution = async () => {
  const res = await request.get('/admin/stats/categories', { params: buildTimeParams() });
  if (res.code === 0) {
    categoryDistribution.value = res.data || [];
    renderCategoryChart();
  }
};

const loadDetail = () => {
  detailList.value = [
    { type: '商品', name: '上架商品数', value: overview.value.totalProductCount },
    { type: '用户', name: '注册用户数', value: overview.value.totalUserCount },
    { type: '卖家', name: '认证卖家数', value: overview.value.totalSellerCount },
    { type: '订单', name: '近7日订单', value: overview.value.sevenDayOrderCount },
  ];
};

const loadAll = async () => {
  await Promise.all([
    loadOverview(),
    loadOrderTrend(),
    loadUserGrowth(),
    loadCategoryDistribution(),
  ]);
  loadDetail();
};

const resetQuery = () => {
  query.value = {
    quickRange: '7d',
    dateRange: [],
  };
  loadAll();
};

const exportReport = async () => {
  const params = buildTimeParams();
  const res = await request.post('/admin/stats/export', null, { params });
  if (res.code === 0 && res.data) {
    window.open(res.data, '_blank');
    ElMessage.success('报表导出已开始下载');
  } else {
    ElMessage.warning(res.message || '报表导出失败');
  }
};

const goBack = () => {
  router.push('/admin/dashboard');
};

onMounted(() => {
  loadAll();
  window.addEventListener('resize', handleResize);
});

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
  const data = categoryDistribution.value.map((p) => ({ name: p.label, value: p.value }));
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
.overview-row {
  margin-bottom: 16px;
}
.overview-item .label {
  color: #909399;
  margin-bottom: 4px;
}
.overview-item .value {
  font-size: 20px;
  font-weight: 600;
}
.chart-row {
  margin-bottom: 16px;
}
.chart-card {
  height: 320px;
}
.chart {
  height: 260px;
}
</style>

