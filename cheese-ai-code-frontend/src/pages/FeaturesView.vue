<template>
  <div class="features-page">
    <div class="hero-section">
      <h1 class="page-title">功能特性</h1>
      <p class="page-subtitle">探索奶酪AI代码平台的强大功能</p>
    </div>

    <div class="features-grid">
      <div class="feature-card" v-for="feature in features" :key="feature.id">
        <div class="feature-icon">{{ feature.icon }}</div>
        <h3 class="feature-title">{{ feature.title }}</h3>
        <p class="feature-description">{{ feature.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Feature {
  id: number
  icon: string
  title: string
  description: string
}

const features: Feature[] = [
  {
    id: 1,
    icon: '🤖',
    title: 'AI智能编程',
    description: '基于先进的AI技术，提供智能代码生成、优化建议和错误修复'
  },
  {
    id: 2,
    icon: '🧀',
    title: '奶酪风味设计',
    description: '独特的奶酪主题UI设计，让编程变得更加有趣和愉悦'
  },
  {
    id: 3,
    icon: '⚡',
    title: '快速开发',
    description: '配置化路由管理，组件化开发，大幅提升开发效率'
  },
  {
    id: 4,
    icon: '🎨',
    title: '现代化界面',
    description: '毛玻璃效果、渐变色彩，打造现代化的用户体验'
  },
  {
    id: 5,
    icon: '📱',
    title: '响应式设计',
    description: '完美适配各种设备，从桌面端到移动端的无缝体验'
  },
  {
    id: 6,
    icon: '🔧',
    title: '可扩展架构',
    description: '基于Vue 3 + TypeScript，支持插件化扩展和自定义开发'
  }
]
</script>

<style scoped>
.features-page {
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

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 140, 66, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(255, 140, 66, 0.2);
  border-color: rgba(255, 140, 66, 0.3);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  filter: drop-shadow(0 4px 8px rgba(255, 140, 66, 0.3));
}

.feature-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.feature-description {
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .features-page {
    padding: 24px 0;
  }

  .page-title {
    font-size: 2.5rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .feature-card {
    padding: 24px 16px;
  }
}
</style>
