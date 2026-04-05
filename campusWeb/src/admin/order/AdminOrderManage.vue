<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="left">
        <span class="title">订单管理</span>
        <span class="sub">全站订单管控</span>
      </div>
      <div class="right">
        <el-button type="primary" @click="goBack">返回后台首页</el-button>
      </div>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="待支付" value="待支付" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
            <el-option label="售后中" value="售后中" />
          </el-select>
        </el-form-item>
        <el-form-item label="买家">
          <el-input v-model="query.buyerKeyword" placeholder="买家姓名/手机号" clearable />
        </el-form-item>
        <el-form-item label="卖家">
          <el-input v-model="query.sellerKeyword" placeholder="卖家姓名/手机号" clearable />
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="query.orderNo" placeholder="订单号" clearable />
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="query.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button @click="loadData">刷新</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="exportOrders">导出订单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="productTitle" label="商品" min-width="200" />
        <el-table-column prop="buyerName" label="买家" width="120" />
        <el-table-column prop="sellerName" label="卖家" width="120" />
        <el-table-column prop="priceText" label="价格" width="100" />
        <el-table-column prop="deliveryTypeText" label="交易方式" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusTagType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button size="small" type="danger" link @click="cancel(scope.row)">取消订单</el-button>
            <el-button size="small" type="warning" link @click="handleAfterSale(scope.row)">处理售后</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          :total="total"
          :current-page="pageNum"
          :page-size="pageSize"
          @current-change="
            (p) => {
              pageNum = p;
              loadData();
            }
          "
          @size-change="
            (s) => {
              pageSize = s;
              pageNum = 1;
              loadData();
            }
          "
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单号">{{ current?.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商品">{{ current?.productTitle }}</el-descriptions-item>
        <el-descriptions-item label="买家">{{ current?.buyerName }}</el-descriptions-item>
        <el-descriptions-item label="卖家">{{ current?.sellerName }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ current?.priceText }}</el-descriptions-item>
        <el-descriptions-item label="交易方式">{{ current?.deliveryTypeText }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ current?.status }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ current?.createdAt }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();

const query = ref({
  status: '',
  buyerKeyword: '',
  sellerKeyword: '',
  orderNo: '',
  dateRange: [],
});

const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);

const detailVisible = ref(false);
const current = ref(null);

const loadData = async () => {
  const params = {
    status: query.value.status,
    buyerKeyword: query.value.buyerKeyword,
    sellerKeyword: query.value.sellerKeyword,
    orderNo: query.value.orderNo,
    pageNum: pageNum.value,
    pageSize: pageSize.value,
  };
  if (query.value.dateRange && query.value.dateRange.length === 2) {
    params.startTime = query.value.dateRange[0];
    params.endTime = query.value.dateRange[1];
  }
  const res = await request.get('/admin/orders', { params });
  if (res.code === 0) {
    list.value = res.data.list || [];
    total.value = res.data.total || 0;
  }
};

const resetQuery = () => {
  query.value = {
    status: '',
    buyerKeyword: '',
    sellerKeyword: '',
    orderNo: '',
    dateRange: [],
  };
  pageNum.value = 1;
  loadData();
};

const statusTagType = (status) => {
  if (status === '已完成') return 'success';
  if (status === '售后中') return 'warning';
  if (status === '已取消') return 'info';
  return '';
};

const viewDetail = async (row) => {
  const res = await request.get(`/admin/orders/${row.id}`);
  if (res.code === 0) {
    current.value = res.data;
    detailVisible.value = true;
  }
};

const cancel = (row) => {
  ElMessageBox.confirm(`确定要取消订单【${row.orderNo}】吗？`, '提示', {
    type: 'warning',
  }).then(async () => {
    await request.post(`/admin/orders/${row.id}/cancel`);
    ElMessage.success('已取消订单');
    loadData();
  }).catch(() => {});
};

const handleAfterSale = (row) => {
  ElMessageBox.confirm(`确认已处理订单【${row.orderNo}】的售后吗？`, '提示', {
    type: 'warning',
  }).then(async () => {
    await request.post(`/admin/orders/${row.id}/after-sale`);
    ElMessage.success('已标记售后处理完成');
    loadData();
  }).catch(() => {});
};

const exportOrders = async () => {
  const res = await request.post('/admin/orders/export', null, {
    params: {
      status: query.value.status,
      buyerKeyword: query.value.buyerKeyword,
      sellerKeyword: query.value.sellerKeyword,
      orderNo: query.value.orderNo,
      startTime: query.value.dateRange?.[0],
      endTime: query.value.dateRange?.[1],
    },
  });
  if (res.code === 0 && res.data) {
    window.open(res.data, '_blank');
    ElMessage.success('订单导出已开始下载');
  } else {
    ElMessage.warning(res.message || '订单导出失败');
  }
};

const goBack = () => {
  router.push('/admin/dashboard');
};

onMounted(() => {
  loadData();
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
.filter-card {
  margin-bottom: 16px;
}
.pagination-bar {
  margin-top: 16px;
  text-align: right;
}
</style>

