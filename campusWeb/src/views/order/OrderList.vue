<template>
  <AppLayout>
    <main class="main">
      <SectionTitle :title="pageTitle" />

      <section class="section">
        <div class="toolbar">
          <el-select
            v-model="status"
            placeholder="订单状态"
            class="w-180"
            clearable
            @change="loadList"
          >
            <el-option label="全部" :value="''" />
            <el-option label="待支付" value="WAIT_PAY" />
            <el-option label="待发货" value="WAIT_SHIP" />
            <el-option label="待收货" value="WAIT_RECEIVE" />
            <el-option label="已完成" value="FINISHED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>

          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="loadList"
          />

          <el-input
            v-model="keyword"
            placeholder="搜索商品 / 对方昵称"
            class="keyword-input"
            clearable
            @keyup.enter="loadList"
            @clear="loadList"
          >
            <template #append>
              <el-button @click="loadList">搜索</el-button>
            </template>
          </el-input>

          <el-button @click="loadList">刷新</el-button>
        </div>
      </section>

      <section class="section">
        <div class="table">
          <div class="header-row">
            <span>订单号</span>
            <span>商品</span>
            <span>金额</span>
            <span>{{ isSellerView ? '买家' : '卖家' }}</span>
            <span>交易方式</span>
            <span>状态</span>
            <span>创建时间</span>
            <span>操作</span>
          </div>
          <div
            class="data-row"
            v-for="item in list"
            :key="item.id"
          >
            <span>{{ item.orderNo }}</span>
            <div class="product-cell">
              <img :src="item.productCoverUrl" alt="" />
              <span>{{ item.productTitle }}</span>
            </div>
            <span>￥{{ item.totalAmount }}</span>
            <span>{{ item.otherPartyName }}</span>
            <span>{{ item.deliveryTypeText }}</span>
            <span>{{ statusLabel(item.status) }}</span>
            <span>{{ item.createdAt }}</span>
            <div class="actions">
              <el-space wrap :size="8" alignment="center">
                <el-button size="small" type="primary" plain @click="goDetail(item.id)">查看详情</el-button>
                <template v-if="!isSellerView">
                  <el-button
                    v-if="item.status === 'WAIT_PAY'"
                    size="small"
                    type="danger"
                    plain
                    @click="cancel(item.id)"
                  >取消订单</el-button>
                  <el-button
                    v-if="item.status === 'WAIT_RECEIVE'"
                    size="small"
                    type="success"
                    plain
                    @click="confirm(item.id)"
                  >确认收货</el-button>
                  <el-button size="small" type="warning" plain @click="afterSale(item.id)">申请售后</el-button>
                </template>
                <template v-else>
                  <el-button
                    v-if="item.status === 'WAIT_SHIP'"
                    size="small"
                    type="primary"
                    plain
                    @click="openShipDialog(item)"
                  >发货</el-button>
                  <el-button size="small" type="danger" plain @click="cancel(item.id)">取消订单</el-button>
                  <el-button size="small" type="warning" plain @click="afterSale(item.id)">处理售后</el-button>
                </template>
              </el-space>
            </div>
          </div>
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
      </section>

      <section class="section">
        <el-button @click="goBack">返回个人中心</el-button>
      </section>
    </main>

    <el-dialog v-model="shipDialogVisible" title="发货" width="400px">
      <el-form :model="shipForm" label-width="90px">
        <el-form-item label="物流单号">
          <el-input v-model="shipForm.logisticsNo" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="ship">确认发货</el-button>
      </template>
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 根据路由参数决定当前视图是“买家订单”还是“卖家订单”。
// /orders -> 我的订单（买家）
// /orders?role=SELLER -> 卖家订单（别人购买我的商品）
const isSellerView = computed(() => route.query.role === 'SELLER');
const pageTitle = computed(() => (isSellerView.value ? '卖家订单' : '我的订单'));

const status = ref('');
const keyword = ref('');
const dateRange = ref([]);
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 10;

const shipDialogVisible = ref(false);
const shipForm = ref({
  id: null,
  logisticsNo: '',
});

function statusLabel(s) {
  switch (s) {
    case 'WAIT_PAY':
      return '待支付';
    case 'WAIT_SHIP':
      return '待发货';
    case 'WAIT_RECEIVE':
      return '待收货';
    case 'SHIPPED':
      return '已发货';
    case 'FINISHED':
    case 'COMPLETED':
      return '已完成';
    case 'CANCELLED':
      return '已取消';
    case 'CREATED':
      return '已创建';
    case 'AFTER_SALE':
      return '售后中';
    case 'AFTER_SALE_HANDLED':
      return '售后已处理';
    default:
      return s || '-';
  }
}

async function loadList() {
  const [startTime, endTime] = dateRange.value || [];
  const url = isSellerView.value ? '/orders/seller' : '/orders/user';
  const res = await request.get(url, {
    params: {
      status: status.value,
      keyword: keyword.value,
      startTime,
      endTime,
      pageNum: pageNum.value,
      pageSize,
    },
  });
  const page = res.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
}

function goDetail(id) {
  const role = isSellerView.value ? 'SELLER' : 'USER';
  router.push({ name: 'OrderDetail', params: { id }, query: { role } });
}

async function cancel(id) {
  const role = isSellerView.value ? 'SELLER' : 'USER';
  await request.post(`/orders/${id}/cancel`, null, { params: { role } });
  ElMessage.success('订单已取消');
  loadList();
}

async function confirm(id) {
  await request.post(`/orders/${id}/confirm`);
  ElMessage.success('已确认收货');
  loadList();
}

async function afterSale(id) {
  await request.post(`/orders/${id}/after-sale`);
  ElMessage.success('售后申请/处理已提交');
}

function openShipDialog(item) {
  shipForm.value.id = item.id;
  shipForm.value.logisticsNo = '';
  shipDialogVisible.value = true;
}

async function ship() {
  await request.post(`/orders/${shipForm.value.id}/ship`, null, {
    params: { logisticsNo: shipForm.value.logisticsNo },
  });
  ElMessage.success('已发货');
  shipDialogVisible.value = false;
  loadList();
}

function goBack() {
  router.push({ name: 'ProfileHome' });
}

onMounted(() => {
  loadList();
});

// 当路由参数中的 role 在“我的订单”和“卖家订单”之间切换时，自动刷新列表
watch(
  () => route.query.role,
  () => {
    pageNum.value = 1;
    loadList();
  },
);
</script>

<style scoped>
.main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1rem;
}

.toolbar {
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

.table {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.75rem 0.75rem 0.25rem;
}

.header-row,
.data-row {
  display: grid;
  grid-template-columns: 1.6fr 2.8fr 1fr 1.4fr 1.3fr 1.1fr 1.8fr 2.2fr;
  gap: 0.75rem;
  padding: 0.35rem 0;
  align-items: center;
  font-size: 0.9rem;
}

.header-row {
  font-weight: 600;
  border-bottom: 1px solid #e5e7eb;
}

.data-row {
  border-bottom: 1px solid #f3f4f6;
}

.data-row:last-child {
  border-bottom: none;
}

.header-row > span,
.data-row > span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.product-cell img {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 8px;
}

.actions {
  display: flex;
  justify-content: flex-start;
}

.actions :deep(.el-button) {
  border-radius: 10px;
  padding: 0 10px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.75rem;
}
</style>

