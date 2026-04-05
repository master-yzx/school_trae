<template>
  <AppLayout>
    <main class="main" v-if="detail">
      <SectionTitle title="订单详情" />

      <section class="section card">
        <div class="row">
          <span>订单号：</span>
          <span>{{ detail.orderNo }}</span>
        </div>
        <div class="row">
          <span>订单状态：</span>
          <span>{{ statusLabel(detail.status) }}</span>
        </div>
        <div class="row">
          <span>创建时间：</span>
          <span>{{ detail.createdAt }}</span>
        </div>
      </section>

      <section class="section card">
        <h3>订单商品</h3>
        <div class="items">
          <div
            class="item-row"
            v-for="(it, idx) in (detail.items || [])"
            :key="`${it.productId}-${idx}`"
          >
            <div class="thumb">
              <img :src="it.productCoverUrl" :alt="it.productTitle" />
            </div>
            <div class="info">
              <div class="title">{{ it.productTitle }}</div>
              <div class="meta">
                <span>单价：￥{{ it.price }}</span>
                <span>数量：{{ it.quantity }}</span>
                <span>小计：￥{{ it.totalAmount }}</span>
              </div>
              <div class="party">
                <span>买家：{{ it.buyerName || detail.buyerName }}</span>
                <span>卖家：{{ it.sellerName || detail.sellerName }}</span>
                <span>交易方式：{{ it.deliveryTypeText || detail.deliveryTypeText }}</span>
                <span v-if="detail.address">收货地址：{{ detail.address }}</span>
                <span v-if="detail.logisticsNo">物流单号：{{ detail.logisticsNo }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="section card">
        <h3>订单操作</h3>
        <div class="actions">
          <template v-if="role === 'USER'">
            <el-button
              v-if="detail.status === 'WAIT_PAY'"
              @click="cancel"
            >取消订单</el-button>
            <el-button
              v-if="detail.status === 'WAIT_PAY'"
              type="primary"
              @click="openPayDialog"
            >去支付</el-button>
            <el-button
              v-if="detail.status === 'WAIT_RECEIVE'"
              type="primary"
              @click="confirm"
            >确认收货</el-button>
            <el-button @click="afterSale">申请售后</el-button>
          </template>
          <template v-else>
            <el-button
              v-if="detail.status === 'WAIT_SHIP'"
              type="primary"
              @click="openShipDialog"
            >发货</el-button>
            <el-button @click="cancel">取消订单</el-button>
            <el-button @click="afterSale">处理售后</el-button>
          </template>
          <el-button @click="contact">联系对方</el-button>
        </div>
      </section>

      <section class="section card">
        <h3>订单日志</h3>
        <ul class="logs">
          <li v-for="log in logs" :key="log.createdAt">
            <span class="time">{{ log.createdAt }}</span>
            <span class="status">{{ statusLabel(log.status) }}</span>
            <span class="msg">{{ log.message }}</span>
          </li>
        </ul>
      </section>

      <section class="section">
        <el-button @click="goBack">返回我的订单</el-button>
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
    <el-dialog v-model="payDialogVisible" title="选择支付方式" width="640px">
      <div v-if="detail" class="pay-body">
        <div class="pay-left">
          <div class="pay-amount">
            应付金额：
            <span class="num">￥{{ detail.totalAmount }}</span>
          </div>
          <div class="pay-order">订单号：{{ detail.orderNo }}</div>
          <div class="pay-methods">
            <div
              v-for="m in payMethods"
              :key="m.value"
              class="pay-method"
              :class="{ active: payMethod === m.value }"
              @click="selectPayMethod(m.value)"
            >
              <span class="pay-icon">
                <img v-if="m.iconUrl" :src="m.iconUrl" :alt="m.label" />
                <span v-else class="initial">{{ m.initial }}</span>
              </span>
              <span class="label">{{ m.label }}</span>
            </div>
          </div>
        </div>
        <div class="pay-right">
          <div class="qr-title">使用 {{ payMethod }} 扫码支付</div>
          <qrcode-vue :value="payQrContent" :size="180" level="M" />
          <div class="qr-tip">当前二维码为示意码，仅用于演示效果</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="pay">我已完成支付</el-button>
      </template>
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';
import QrcodeVue from 'qrcode.vue';

const route = useRoute();
const router = useRouter();

const id = route.params.id;
const role = route.query.role || 'USER';

const detail = ref(null);
const logs = ref([]);

const shipDialogVisible = ref(false);
const shipForm = ref({
  logisticsNo: '',
});

const payDialogVisible = ref(false);
const payMethod = ref('微信支付');
const payMethods = [
  {
    label: '微信支付',
    value: '微信支付',
    iconUrl: 'https://logo-teka.com/wp-content/uploads/2026/01/wechat-pay-horizontal-logo.png',
  },
  {
    label: '支付宝',
    value: '支付宝',
    iconUrl: 'https://logo-teka.com/wp-content/uploads/2026/01/alipay-icon-logo-no-background.png',
  },
  {
    label: '云闪付',
    value: '云闪付',
    initial: '云',
  },
  {
    label: '银行卡支付',
    value: '银行卡支付',
    initial: '卡',
  },
  {
    label: '现金当面付',
    value: '现金当面付',
    initial: '现',
  },
  {
    label: '校园一卡通',
    value: '校园一卡通',
    initial: '校',
  },
];

const payQrContent = computed(() => {
  if (!detail.value) return '';
  const base = `ORDER:${detail.value.orderNo || id}`;
  const method = payMethod.value;
  const salt = Math.random().toString(36).slice(2, 10);
  return `${base}|METHOD:${method}|${salt}`;
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

async function loadData() {
  const [detailRes, logsRes] = await Promise.all([
    request.get(`/orders/${id}`, { params: { role } }),
    request.get(`/orders/${id}/logs`),
  ]);
  detail.value = detailRes.data || null;
  logs.value = logsRes.data || [];
}

async function cancel() {
  await request.post(`/orders/${id}/cancel`, null, { params: { role } });
  ElMessage.success('订单已取消');
  loadData();
}

async function confirm() {
  await request.post(`/orders/${id}/confirm`);
  ElMessage.success('已确认收货');
  loadData();
}

async function afterSale() {
  await request.post(`/orders/${id}/after-sale`);
  ElMessage.success('售后申请/处理已提交');
}

function openPayDialog() {
  payDialogVisible.value = true;
}

async function pay() {
  await request.post(`/orders/${id}/pay`, null, {
    params: { method: payMethod.value },
  });
  ElMessage.success('支付成功');
  payDialogVisible.value = false;
  loadData();
}

function selectPayMethod(v) {
  payMethod.value = v;
}

function openShipDialog() {
  shipForm.value.logisticsNo = '';
  shipDialogVisible.value = true;
}

async function ship() {
  await request.post(`/orders/${id}/ship`, null, {
    params: { logisticsNo: shipForm.value.logisticsNo },
  });
  ElMessage.success('已发货');
  shipDialogVisible.value = false;
  loadData();
}

async function contact() {
  try {
    const res = await request.post('/chat/start', { orderId: id });
    const sessionId = (res && res.data !== undefined) ? res.data : res;
    if (sessionId) {
      router.push({ name: 'ChatCenter', query: { sessionId } });
    } else {
      ElMessage.warning('无法发起聊天，请稍后重试');
    }
  } catch (e) {
    ElMessage.warning(e?.response?.data?.message || '请先登录后再联系对方');
  }
}

function goBack() {
  router.push({ name: 'OrderList' });
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.main {
  max-width: 800px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
}

.section + .section {
  margin-top: 1rem;
}

.card {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.85rem 1rem;
}

.row {
  display: flex;
  gap: 0.5rem;
  font-size: 0.95rem;
}

.thumb img {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border-radius: 8px;
}

.title {
  font-size: 1rem;
  font-weight: 500;
  margin-bottom: 0.4rem;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  font-size: 0.9rem;
  color: #4b5563;
}

.items {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.item-row {
  display: flex;
  gap: 0.75rem;
  padding: 0.25rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.item-row:last-child {
  border-bottom: none;
}

.party {
  margin-top: 0.45rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem 1rem;
  font-size: 0.85rem;
  color: #6b7280;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.logs {
  list-style: none;
  padding: 0;
  margin: 0;
}

.logs li {
  display: flex;
  gap: 0.75rem;
  font-size: 0.9rem;
  padding: 0.25rem 0;
}

.time {
  color: #6b7280;
}

.status {
  font-weight: 500;
}

.pay-body {
  display: flex;
  gap: 1.25rem;
}

.pay-left {
  flex: 1;
}

.pay-right {
  width: 220px;
  text-align: center;
}

.pay-amount {
  font-size: 0.98rem;
  margin-bottom: 0.25rem;
}

.pay-amount .num {
  color: #ef4444;
  font-weight: 600;
  font-size: 1.25rem;
}

.pay-order {
  font-size: 0.85rem;
  color: #6b7280;
}

.pay-methods {
  margin-top: 0.75rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.pay-method {
  display: inline-flex;
  align-items: center;
  padding: 0.3rem 0.6rem;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  font-size: 0.86rem;
}

.pay-method .pay-icon {
  width: 22px;
  height: 22px;
  border-radius: 6px;
  margin-right: 6px;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #f3f4f6;
}

.pay-method .pay-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.pay-method .pay-icon .initial {
  font-size: 0.8rem;
  color: #111827;
}

.pay-method.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.qr-title {
  font-size: 0.9rem;
  margin-bottom: 0.4rem;
}

.qr-tip {
  margin-top: 0.4rem;
  font-size: 0.78rem;
  color: #9ca3af;
}
</style>

