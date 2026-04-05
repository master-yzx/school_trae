<template>
  <router-view />
</template>

<script setup>
</script>

<style>
*,
*::before,
*::after {
  box-sizing: border-box;
}

html,
body {
  margin: 0;
  padding: 0;
}

body {
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: #e6f0fa;
}

#app {
  min-height: 100vh;
}

/* 让每个页面的底部栏始终贴在视口底部 */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page > main {
  flex: 1 0 auto;
}
</style>


