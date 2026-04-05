<template>
  <div class="user-info">
    <!-- 头像区域 -->
    <div @click="chooseAvatar" class="avatar-container">
      <van-image round width="60" height="60" :src="avatarUrl" />
      <input type="file" ref="fileInput" accept="image/*" @change="handleFileChange" style="display: none;" />
    </div>

    <!-- 用户信息 -->
    <div class="user-details">
      <div>
        <span>{{ userName }}</span>
        <van-tag plain type="primary">{{ userRole }}</van-tag>
      </div>
      <div>
        学号：{{ studentId }} &nbsp;
        手机：{{ phoneNumber }} &nbsp;
        院校：{{ campus }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      avatarUrl: 'https://placehold.co/60x60', // 默认头像占位图
      userName: '示例用户',
      userRole: '管理员',
      studentId: '20****01',
      phoneNumber: '138****0000',
      campus: '东校区'
    };
  },
  methods: {
    chooseAvatar() {
      this.$refs.fileInput.click();
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.avatarUrl = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    }
  }
};
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
}

.avatar-container {
  margin-right: 15px;
  cursor: pointer;
}

.user-details {
  flex: 1;
}
</style>