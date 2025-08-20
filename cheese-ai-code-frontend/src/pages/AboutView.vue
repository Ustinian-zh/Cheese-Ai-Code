<template>
  <div class="about-page">
    <div class="hero-section">
      <h1 class="page-title">关于我们</h1>
      <p class="page-subtitle">了解奶酪AI代码平台的故事</p>
    </div>

    <div class="content-section">
      <div class="story-card">
        <h2 class="section-title">我们的故事</h2>
        <p class="section-content">
          奶酪AI代码平台诞生于对编程的热爱和对美食的向往。我们相信，编程就像制作奶酪一样，
          需要精心的配方、耐心的等待，以及对品质的执着追求。
        </p>
        <p class="section-content">
          通过将AI技术与奶酪的温暖理念相结合，我们致力于创造一个既专业又充满趣味的编程环境，
          让每一位开发者都能在这里找到属于自己的"奶酪味道"。
        </p>
      </div>

      <div class="mission-card">
        <h2 class="section-title">我们的使命</h2>
        <div class="mission-grid">
          <div class="mission-item">
            <div class="mission-icon">🎯</div>
            <h3>专业至上</h3>
            <p>提供最前沿的AI编程工具和解决方案</p>
          </div>
          <div class="mission-item">
            <div class="mission-icon">❤️</div>
            <h3>用户体验</h3>
            <p>打造温暖友好的开发者社区</p>
          </div>
          <div class="mission-item">
            <div class="mission-icon">🚀</div>
            <h3>持续创新</h3>
            <p>不断探索编程的新可能</p>
          </div>
          <div class="mission-item">
            <div class="mission-icon">🧀</div>
            <h3>奶酪精神</h3>
            <p>将温暖和趣味注入技术世界</p>
          </div>
        </div>
      </div>

      <div class="team-card">
        <h2 class="section-title">核心技术</h2>
        <div class="tech-stack">
          <div class="tech-item">Vue 3</div>
          <div class="tech-item">TypeScript</div>
          <div class="tech-item">Ant Design Vue</div>
          <div class="tech-item">Vite</div>
          <div class="tech-item">Pinia</div>
          <div class="tech-item">Vue Router</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 这个页面展示配置化路由的自动生成效果
console.log('关于我们页面已通过配置化路由自动注册')
</script>

<style scoped>
.about-page {
  padding: 40px 0;
}

.hero-section {
  text-align: center;
  margin-bottom: 60px;
}

.page-title {
  font-size: 3rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 50%, #ffb74d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.page-subtitle {
  font-size: 1.2rem;
  color: #64748b;
  margin-bottom: 0;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.story-card,
.mission-card,
.team-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 32px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 140, 66, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 24px;
  text-align: center;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.section-content {
  color: #64748b;
  line-height: 1.8;
  margin-bottom: 16px;
  text-align: justify;
}

.mission-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.mission-item {
  text-align: center;
  padding: 20px;
  background: rgba(255, 140, 66, 0.05);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.mission-item:hover {
  background: rgba(255, 140, 66, 0.1);
  transform: translateY(-4px);
}

.mission-icon {
  font-size: 2rem;
  margin-bottom: 12px;
}

.mission-item h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.mission-item p {
  color: #64748b;
  font-size: 0.9rem;
  line-height: 1.5;
  margin: 0;
}

.tech-stack {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.tech-item {
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 100%);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
  font-size: 0.9rem;
  box-shadow: 0 2px 8px rgba(255, 140, 66, 0.3);
  transition: all 0.3s ease;
}

.tech-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 140, 66, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .about-page {
    padding: 24px 0;
  }

  .page-title {
    font-size: 2.5rem;
  }

  .story-card,
  .mission-card,
  .team-card {
    padding: 24px 16px;
  }

  .mission-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
