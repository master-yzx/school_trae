<template>
  <AppLayout>
    <main class="forum-main">
      <section class="forum-card">
        <header class="forum-header">
          <div class="forum-title">校园论坛</div>
          <div class="forum-subtitle">分享经验 · 寻找同好 · 交流交易心得</div>
          <el-button type="primary" @click="openCreateDialog">发帖</el-button>
        </header>

        <el-tabs v-model="activeTab" class="forum-tabs" @tab-change="loadPosts">
          <el-tab-pane label="最新" name="latest" />
          <el-tab-pane label="最热" name="hot" />
        </el-tabs>

        <el-empty v-if="!loading && posts.length === 0" description="还没有帖子，快来抢沙发～" />

        <el-skeleton v-else-if="loading" :rows="4" animated />

        <div v-else class="post-list">
          <article
            v-for="item in posts"
            :key="item.id"
            class="post-item"
            @click="goDetail(item.id)"
          >
            <div class="post-main">
              <h3 class="post-title">{{ item.title }}</h3>
              <p class="post-content">{{ item.summary }}</p>
              <div class="post-meta">
                <span>作者：{{ item.authorNickname || '匿名用户' }}</span>
                <span>发布时间：{{ item.createdAt }}</span>
              </div>
            </div>
            <div class="post-stats">
              <div class="stat">
                <span class="stat-num">{{ item.replyCount }}</span>
                <span class="stat-label">回复</span>
              </div>
              <div class="stat">
                <span class="stat-num">{{ item.viewCount }}</span>
                <span class="stat-label">浏览</span>
              </div>
              <div class="stat">
                <span class="stat-num">{{ item.likeCount }}</span>
                <span class="stat-label">点赞</span>
              </div>
            </div>
          </article>
        </div>
      </section>

      <el-dialog v-model="createVisible" title="发表新帖" width="720px">
        <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="90px">
          <el-form-item label="标题" prop="title">
            <el-input v-model="createForm.title" maxlength="80" show-word-limit />
          </el-form-item>
          <el-form-item label="关联商品">
            <div class="bind-product-row">
              <el-input
                v-model="createForm.productDisplay"
                placeholder="可选：选择要绑定的商品"
                readonly
              />
              <el-button class="ml8" @click="openProductPicker">选择商品</el-button>
              <el-button v-if="createForm.productId" text type="danger" @click="clearProduct">
                清除
              </el-button>
            </div>
          </el-form-item>
          <el-form-item label="图文内容" prop="content">
            <div class="editor-wrapper">
              <Toolbar
                class="editor-toolbar"
                :editor="editorRef"
                :defaultConfig="toolbarConfig"
                mode="default"
              />
              <Editor
                v-model="createForm.content"
                class="editor-body"
                :defaultConfig="editorConfig"
                mode="default"
                @onCreated="handleEditorCreated"
              />
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="createVisible = false">取消</el-button>
          <el-button type="primary" :loading="publishing" @click="submitPost">发布</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="productPickerVisible" title="选择关联商品" width="800px">
        <div class="picker-toolbar">
          <el-input
            v-model="productSearch.keyword"
            placeholder="输入关键词搜索商品"
            class="picker-keyword"
            clearable
            @keyup.enter="loadProducts"
          >
            <template #append>
              <el-button type="primary" @click="loadProducts">搜索</el-button>
            </template>
          </el-input>
        </div>
        <el-table
          :data="productList"
          stripe
          height="380"
          @row-dblclick="handlePickProduct"
        >
          <el-table-column prop="title" label="标题" min-width="220" />
          <el-table-column prop="campusName" label="校园" width="120" />
          <el-table-column prop="sellerName" label="卖家" width="120" />
          <el-table-column prop="price" label="价格(元)" width="100">
            <template #default="{ row }">
              {{ row.price }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" text @click="handlePickProduct(row)">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="picker-pagination">
          <el-pagination
            background
            layout="total, prev, pager, next"
            :total="productTotal"
            :page-size="productSearch.pageSize"
            :current-page="productSearch.pageNum"
            @current-change="(p) => { productSearch.pageNum = p; loadProducts(); }"
          />
        </div>
      </el-dialog>
    </main>
  </AppLayout>
</template>

<script setup>
import { onMounted, ref, shallowRef } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import request from '../../utils/request';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import '@wangeditor/editor/dist/css/style.css';

const router = useRouter();

const activeTab = ref('latest');
const loading = ref(false);
const posts = ref([]);

const createVisible = ref(false);
const createFormRef = ref();
const createForm = ref({
  title: '',
  content: '',
  productId: '',
  productDisplay: '',
});
const createRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'change' }],
};
const publishing = ref(false);
const productPickerVisible = ref(false);
const productSearch = ref({
  keyword: '',
  pageNum: 1,
  pageSize: 8,
});
const productList = ref([]);
const productTotal = ref(0);

async function loadProducts() {
  try {
    const res = await request.get('/products/search', { params: productSearch.value });
    const page = res.data || {};
    productList.value = page.list || page.records || page.items || [];
    productTotal.value = page.total || 0;
  } catch (e) {
    ElMessage.error('加载商品列表失败，请稍后重试');
  }
}

function openProductPicker() {
  productPickerVisible.value = true;
  productSearch.value.pageNum = 1;
  loadProducts();
}

function handlePickProduct(row) {
  if (!row) return;
  createForm.value.productId = row.id;
  createForm.value.productDisplay = `${row.title}（¥${row.price}）`;
  productPickerVisible.value = false;
}

function clearProduct() {
  createForm.value.productId = '';
  createForm.value.productDisplay = '';
}

const editorRef = shallowRef(null);
const toolbarConfig = {};
const editorConfig = {
  placeholder: '可以插入图片、文字，并在文中介绍相关商品、交易经验等...',
  MENU_CONF: {
    uploadImage: {
      // 自定义图片上传到现有上传接口
      async customUpload(file, insertFn) {
        try {
          const formData = new FormData();
          formData.append('file', file);
          const res = await request.post('/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
          });
          const data = res.data || {};
          if (!data.url) {
            ElMessage.error('图片上传失败');
            return;
          }
          insertFn(data.url, '图片', data.url);
        } catch (e) {
          ElMessage.error('图片上传失败，请稍后重试');
        }
      },
    },
  },
};

function handleEditorCreated(editor) {
  editorRef.value = editor;
}

async function loadPosts() {
  loading.value = true;
  try {
    const res = await request.get('/forum/posts', {
      params: { sort: activeTab.value },
    });
    const data = res.data || [];
    posts.value = data;
  } catch (e) {
    ElMessage.error('加载论坛列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
}

function goDetail(id) {
  router.push({ name: 'ForumDetail', params: { id } });
}

function openCreateDialog() {
  createVisible.value = true;
}

function submitPost() {
  createFormRef.value.validate(async (valid) => {
    if (!valid) return;
    publishing.value = true;
    try {
      const payload = {
        title: createForm.value.title,
        content: createForm.value.content,
        productId: createForm.value.productId ? Number(createForm.value.productId) : null,
      };
      const res = await request.post('/forum/posts', payload);
      if (res && res.code !== 0) {
        ElMessage.error(res.message || '发布失败，请稍后重试');
        return;
      }
      ElMessage.success('发布成功');
      createVisible.value = false;
      createForm.value.title = '';
      createForm.value.content = '';
      createForm.value.productId = '';
      createForm.value.productDisplay = '';
      await loadPosts();
    } catch (e) {
      ElMessage.error(e?.response?.data?.message || e?.message || '发布失败，请稍后重试');
    } finally {
      publishing.value = false;
    }
  });
}

onMounted(() => {
  loadPosts();
});
</script>

<style scoped>
.forum-main {
  max-width: 1120px;
  margin: 24px auto;
  padding: 0 16px 40px;
}

.forum-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px 24px 24px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04);
}

.forum-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.forum-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

.forum-subtitle {
  flex: 1;
  margin-left: 16px;
  font-size: 13px;
  color: #6b7280;
}

.forum-tabs {
  margin-bottom: 12px;
}

.post-list {
  margin-top: 8px;
}

.post-item {
  display: flex;
  padding: 12px 4px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
}

.post-item:last-child {
  border-bottom: none;
}

.post-item:hover {
  background: #f9fafb;
}

.post-main {
  flex: 1;
  padding-right: 16px;
}

.post-title {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.post-content {
  margin: 0 0 6px;
  font-size: 13px;
  color: #4b5563;
  max-height: 40px;
  overflow: hidden;
}

.post-meta {
  font-size: 12px;
  color: #9ca3af;
  display: flex;
  gap: 16px;
}

.post-stats {
  width: 120px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #2563eb;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #6b7280;
}
</style>

