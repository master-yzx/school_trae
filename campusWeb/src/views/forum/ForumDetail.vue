<template>
  <AppLayout>
    <main class="forum-main">
      <section class="forum-card" v-if="post">
        <header class="post-header">
          <h1 class="post-title">{{ post.title }}</h1>
          <div class="post-meta">
            <span>作者：{{ post.authorNickname || '匿名用户' }}</span>
            <span>发布时间：{{ post.createdAt }}</span>
            <span>浏览：{{ post.viewCount }}</span>
          </div>
        </header>
        <article class="post-content">
          <div class="post-html" v-html="post.content" />
        </article>
        <div v-if="post.productId" class="post-product">
          <el-button type="primary" link @click="goProduct(post.productId)">查看关联商品</el-button>
        </div>
        <footer class="post-actions">
          <el-button
            type="text"
            :icon="liked ? '👍' : '👍'"
            @click="toggleLike"
            :loading="liking"
          >
            点赞 {{ post.likeCount }}
          </el-button>
        </footer>
      </section>

      <section class="forum-card">
        <header class="comment-header">
          <div class="comment-title">回复</div>
        </header>

        <el-empty
          v-if="!loadingComments && comments.length === 0"
          description="还没有回复，来说句话吧～"
        />

        <el-skeleton v-else-if="loadingComments" :rows="3" animated />

        <ul v-else class="comment-list">
          <li v-for="item in comments" :key="item.id" class="comment-item">
            <div class="comment-main">
              <div class="comment-meta">
                <span class="comment-author">{{ item.authorNickname || '匿名用户' }}</span>
                <span class="comment-time">{{ item.createdAt }}</span>
              </div>
              <div class="comment-content">{{ item.content }}</div>
            </div>
          </li>
        </ul>

        <div class="comment-editor">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="说点什么吧～"
            maxlength="500"
            show-word-limit
          />
          <div class="comment-actions">
            <el-button type="primary" :loading="commenting" @click="submitComment">发表评论</el-button>
          </div>
        </div>
      </section>
    </main>
  </AppLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import AppLayout from '../../components/layout/AppLayout.vue';
import request from '../../utils/request';

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const post = ref(null);
const comments = ref([]);
const loadingPost = ref(false);
const loadingComments = ref(false);
const commentContent = ref('');
const commenting = ref(false);
const liking = ref(false);
const liked = ref(false);

async function loadPost() {
  loadingPost.value = true;
  try {
    const res = await request.get(`/forum/posts/${id}`);
    post.value = res.data || null;
  } catch (e) {
    ElMessage.error('加载帖子失败，请稍后重试');
  } finally {
    loadingPost.value = false;
  }
}

async function loadComments() {
  loadingComments.value = true;
  try {
    const res = await request.get(`/forum/posts/${id}/comments`);
    comments.value = res.data || [];
  } catch (e) {
    ElMessage.error('加载回复失败，请稍后重试');
  } finally {
    loadingComments.value = false;
  }
}

async function submitComment() {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  commenting.value = true;
  try {
    await request.post(`/forum/posts/${id}/comments`, {
      content: commentContent.value,
    });
    commentContent.value = '';
    await loadComments();
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '发表评论失败，请稍后重试');
  } finally {
    commenting.value = false;
  }
}

async function toggleLike() {
  if (!post.value) return;
  liking.value = true;
  try {
    const res = await request.post(`/forum/posts/${id}/like`);
    const ok = res?.code === 0;
    if (ok) {
      liked.value = !liked.value;
      const delta = liked.value ? 1 : -1;
      post.value.likeCount = Math.max(0, (post.value.likeCount || 0) + delta);
    }
  } catch (e) {
    ElMessage.error('点赞失败，请稍后重试');
  } finally {
    liking.value = false;
  }
}

function goProduct(productId) {
  router.push({ name: 'ProductDetail', params: { id: productId } });
}

onMounted(() => {
  loadPost();
  loadComments();
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
  padding: 16px 24px 20px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04);
  margin-bottom: 16px;
}

.post-header {
  margin-bottom: 8px;
}

.post-title {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

.post-meta {
  font-size: 12px;
  color: #9ca3af;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.post-content {
  margin-top: 8px;
}

.post-text {
  margin: 0;
  white-space: pre-wrap;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.post-actions {
  margin-top: 10px;
  border-top: 1px solid #f3f4f6;
  padding-top: 8px;
  display: flex;
  justify-content: flex-start;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.comment-list {
  list-style: none;
  padding: 0;
  margin: 0 0 12px;
}

.comment-item {
  padding: 10px 0;
  border-bottom: 1px solid #f3f4f6;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-meta {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
  display: flex;
  gap: 12px;
}

.comment-author {
  font-weight: 500;
  color: #4b5563;
}

.comment-content {
  font-size: 14px;
  color: #374151;
}

.comment-editor {
  margin-top: 8px;
}

.comment-actions {
  text-align: right;
  margin-top: 6px;
}
</style>

