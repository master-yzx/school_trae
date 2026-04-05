<template>
  <AppLayout>
    <main class="main">
      <SectionTitle title="我的购物车" />

      <section class="section">
        <div class="top-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索购物车中的商品"
            class="keyword-input"
            clearable
            @keyup.enter="loadList"
            @clear="loadList"
          >
            <template #append>
              <el-button @click="loadList">搜索</el-button>
            </template>
          </el-input>

          <el-checkbox v-model="checkAll" @change="toggleCheckAll">
            全选
          </el-checkbox>

          <el-button type="danger" plain @click="clearCart">
            清空购物车
          </el-button>
        </div>
      </section>

      <section class="section">
        <div class="cart-area">
          <div
            v-for="item in list"
            :key="item.id"
            class="cart-row"
          >
            <el-checkbox v-model="selectedIds" :label="item.id" />
            <div class="thumb">
              <img :src="item.coverUrl" :alt="item.title" />
            </div>
            <div class="info">
              <div class="title">{{ item.title }}</div>
              <div class="meta">
                <span>卖家：{{ item.sellerName }}</span>
                <span v-if="item.campusName">校园：{{ item.campusName }}</span>
                <span v-if="item.deliveryTypeText">{{ item.deliveryTypeText }}</span>
              </div>
            </div>
            <div class="price">￥{{ item.price }}</div>
            <div class="qty">
              <el-input-number
                v-model="item.quantity"
                :min="1"
                :max="99"
                size="small"
                controls-position="right"
                @change="(val) => changeQuantity(item, val)"
              />
            </div>
            <div class="row-actions">
              <el-button text type="primary" @click="goDetail(item.productId)">查看详情</el-button>
              <el-button text type="danger" @click="removeOne(item.id)">删除</el-button>
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
        </div>
      </section>

      <section class="section footer-bar">
        <div class="summary">
          已选 {{ selectedCount }} 件商品，合计：
          <span class="sum">￥{{ totalAmount }}</span>
        </div>
        <el-button type="primary" @click="settle">去结算</el-button>
        <el-button @click="goBack">返回个人中心</el-button>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import SectionTitle from '../../components/common/SectionTitle.vue';
import request from '../../utils/request';

const router = useRouter();

const keyword = ref('');
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = 10;

const selectedIds = ref([]);
const checkAll = ref(false);

const selectedItems = computed(() =>
  list.value.filter((item) => selectedIds.value.includes(item.id)),
);
const selectedCount = computed(() => selectedItems.value.length);
const totalAmount = computed(() =>
  selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0),
);

async function loadList() {
  const res = await request.get('/cart/list', {
    params: { keyword: keyword.value, pageNum: pageNum.value, pageSize },
  });
  const page = res.data || {};
  list.value = page.list || [];
  total.value = page.total || 0;
  // 默认全选当前页
  selectedIds.value = list.value.map((i) => i.id);
  checkAll.value = selectedIds.value.length === list.value.length;
}

function toggleCheckAll(val) {
  if (val) {
    selectedIds.value = list.value.map((i) => i.id);
  } else {
    selectedIds.value = [];
  }
}

function clearCart() {
  ElMessageBox.confirm('确认清空购物车吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete('/cart/clear');
        ElMessage.success('购物车已清空');
        pageNum.value = 1;
        await loadList();
      } catch (e) {
        const raw = e?.response?.data;
        const msg = raw?.message || raw?.msg || '清空失败，请先登录';
        ElMessage.warning(msg);
      }
    })
    .catch(() => {});
}

async function removeOne(id) {
  await request.delete(`/cart/${id}`);
  ElMessage.success('已删除该商品');
  loadList();
}

async function changeQuantity(item, val) {
  try {
    await request.post(`/cart/${item.id}/quantity`, null, { params: { quantity: val } });
    ElMessage.success('数量已更新');
  } catch (e) {
    ElMessage.warning(e?.response?.data?.message || '更新数量失败');
  }
}

async function settle() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择要结算的商品');
    return;
  }
  const items = selectedItems.value;
  try {
    const payload = {
      items: items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      })),
    };
    const res = await request.post('/orders/create-from-cart', payload);

    // 兼容后端统一 ApiResponse 包装：code=0 成功，其余视为业务失败
    let orderId;
    if (res && typeof res === 'object' && Object.prototype.hasOwnProperty.call(res, 'code')) {
      if (res.code !== 0) {
        let msg = res.message || '结算失败，请稍后重试';
        // 去掉前缀里的 HTTP 状态信息，只保留业务文案
        const marker = '\"';
        const firstQuote = msg.indexOf(marker);
        const lastQuote = msg.lastIndexOf(marker);
        if (firstQuote !== -1 && lastQuote > firstQuote) {
          msg = msg.slice(firstQuote + 1, lastQuote);
        }
        ElMessage.warning(msg);
        return;
      }
      orderId = res.data;
    } else {
      orderId = res;
    }
    if (!orderId) {
      ElMessage.warning('结算失败，请稍后重试');
      return;
    }
    // 订单创建成功后，删除对应购物车条目
    for (const item of items) {
      await request.delete(`/cart/${item.id}`);
    }
    ElMessage.success('已为选中商品创建订单，正在跳转支付页面');
    router.push({ name: 'OrderDetail', params: { id: orderId }, query: { role: 'USER' } });
  } catch (e) {
    const raw = e?.response?.data;
    let msg = raw?.message || raw?.msg || '结算失败，请稍后重试';
    ElMessage.warning(msg);
  }
}

function goDetail(productId) {
  router.push({ name: 'ProductDetail', params: { id: productId } });
}

function goBack() {
  router.push({ name: 'ProfileHome' });
}

onMounted(() => {
  loadList();
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

.cart-area {
  background: #ffffff;
  border-radius: 12px;
  padding: 0.75rem 0.75rem 1rem;
}

.cart-row {
  display: grid;
  grid-template-columns: auto 100px minmax(0, 1.6fr) 90px 110px 140px;
  gap: 0.75rem;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.cart-row :deep(.el-checkbox__label) {
  display: none;
}

.cart-row:last-child {
  border-bottom: none;
}

.thumb img {
  width: 100%;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.title {
  font-size: 0.98rem;
  font-weight: 500;
  margin-bottom: 0.35rem;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem 1rem;
  font-size: 0.85rem;
  color: #4b5563;
}

.price {
  color: #ef4444;
  font-weight: 600;
}

.row-actions {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  align-items: flex-end;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.75rem;
}

.footer-bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
}

.summary {
  font-size: 0.95rem;
}

.sum {
  color: #ef4444;
  font-weight: 700;
  margin-left: 0.25rem;
}
</style>

