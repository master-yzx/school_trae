<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="left">
        <span class="title">操作日志</span>
        <span class="sub">平台操作记录查询</span>
      </div>
      <div class="right">
        <el-button type="primary" @click="goBack">返回后台首页</el-button>
      </div>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="操作人">
          <el-input v-model="query.operator" placeholder="用户名/手机号" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable>
            <el-option label="登录" value="登录" />
            <el-option label="订单操作" value="订单操作" />
            <el-option label="系统配置" value="系统配置" />
          </el-select>
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="query.result" placeholder="全部" clearable>
            <el-option label="成功" value="成功" />
            <el-option label="失败" value="失败" />
          </el-select>
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
        <el-form-item label="内容">
          <el-input v-model="query.keyword" placeholder="操作内容关键字" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button @click="loadData">刷新</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="exportLogs">导出日志</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="operator" label="操作人" width="130" />
        <el-table-column prop="type" label="类型" width="130" />
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="120" />
        <el-table-column prop="result" label="结果" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.result === '成功' ? 'success' : 'danger'">
              {{ scope.row.result }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="viewDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
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

    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作人">{{ current?.operator }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ current?.type }}</el-descriptions-item>
        <el-descriptions-item label="结果">{{ current?.result }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ current?.ip }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ current?.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ current?.content }}</el-descriptions-item>
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
import { ElMessage } from 'element-plus';

const router = useRouter();

const query = ref({
  operator: '',
  type: '',
  result: '',
  keyword: '',
  dateRange: [],
});

const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);

const detailVisible = ref(false);
const current = ref(null);

const buildParams = () => {
  const params = {
    operator: query.value.operator,
    type: query.value.type,
    result: query.value.result,
    keyword: query.value.keyword,
    pageNum: pageNum.value,
    pageSize: pageSize.value,
  };
  if (query.value.dateRange && query.value.dateRange.length === 2) {
    params.startTime = query.value.dateRange[0];
    params.endTime = query.value.dateRange[1];
  }
  return params;
};

const loadData = async () => {
  const res = await request.get('/admin/logs', { params: buildParams() });
  if (res.code === 0) {
    list.value = res.data.list || [];
    total.value = res.data.total || 0;
  }
};

const resetQuery = () => {
  query.value = {
    operator: '',
    type: '',
    result: '',
    keyword: '',
    dateRange: [],
  };
  pageNum.value = 1;
  loadData();
};

const viewDetail = async (row) => {
  const res = await request.get(`/admin/logs/${row.id}`);
  if (res.code === 0) {
    current.value = res.data;
    detailVisible.value = true;
  }
};

const exportLogs = async () => {
  const res = await request.post('/admin/logs/export', buildParams());
  if (res.code === 0 && res.data) {
    // 后端返回的是可直接访问的文件 URL，例如 /upload/operation-log-xxx.csv
    window.open(res.data, '_blank');
    ElMessage.success('日志导出已开始下载');
  } else {
    ElMessage.warning(res.message || '日志导出失败，请稍后重试');
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

