<script setup lang="ts">
// 简化的首页，暂时无需任何API调用
</script>

<template>
  <main>
    <div class="home-container">
      <div class="hero-section">
        <h1 class="main-title">🧀 奶酪AI代码平台</h1>
        <p class="subtitle">智能编程助手平台</p>
      </div>
    </div>
  </main>
</template>

<style scoped>
.home-container {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.hero-section {
  text-align: center;
  max-width: 800px;
}

.main-title {
  font-size: 3.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 50%, #ffb74d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.subtitle {
  font-size: 1.3rem;
  color: #64748b;
  margin-bottom: 48px;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-title {
    font-size: 2.8rem;
  }
}
</style>
