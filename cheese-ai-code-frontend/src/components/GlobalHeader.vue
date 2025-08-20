<template>
  <a-layout-header class="header">
    <div class="header-content">
      <!-- 左侧 Logo 和标题 -->
      <div class="logo-section">
        <img src="@/assets/logo.png" alt="奶酪AI代码平台" class="logo" />
        <span class="site-title">奶酪AI代码平台</span>
      </div>

      <!-- 中间菜单 -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        class="main-menu"
        :items="menuItems"
        @click="handleMenuClick"
      />

      <!-- 右侧用户区域 -->
      <div class="user-section">
        <a-button
          type="text"
          class="locale-btn"
          @click="toggleLocale"
          :title="isZhCN ? '切换到英文' : 'Switch to Chinese'"
        >
          {{ isZhCN ? 'EN' : '中' }}
        </a-button>
        <a-button type="primary" class="login-btn" @click="handleLogin">
          {{ isZhCN ? '登录' : 'Login' }}
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useMenu } from '@/composables/useMenu'
import { useLocale } from '@/composables/useLocale'

const route = useRoute()
const { menuItems, selectedKeys, handleMenuClick, updateSelectedKeys } = useMenu()
const { isZhCN, toggleLocale } = useLocale()

// 登录按钮处理
const handleLogin = () => {
  console.log('点击登录按钮')
  // 这里可以添加登录逻辑
}

// 组件挂载时更新选中状态
onMounted(() => {
  updateSelectedKeys()
})

// 监听路由变化
watch(() => route.name, () => {
  updateSelectedKeys()
})
</script>

<style scoped>
.header {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 8px rgba(0, 21, 41, 0.12);
  border-bottom: 1px solid rgba(255, 140, 66, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  padding: 0;
  backdrop-filter: blur(8px);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 200px;
}

.logo {
  height: 48px;
  width: 48px;
  filter: drop-shadow(0 3px 6px rgba(255, 165, 0, 0.4));
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05) rotate(5deg);
}

.site-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  white-space: nowrap;
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 50%, #ffb74d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.8px;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  text-shadow: 0 2px 4px rgba(255, 140, 66, 0.2);
}

.main-menu {
  flex: 1;
  border-bottom: none;
  display: flex;
  justify-content: center;
  min-width: 0;
  background: transparent;
}

.main-menu :deep(.ant-menu-item) {
  color: #64748b;
  font-weight: 500;
  transition: all 0.3s ease;
}

.main-menu :deep(.ant-menu-item:hover) {
  color: #ff8c42;
  background: rgba(255, 140, 66, 0.1);
}

.main-menu :deep(.ant-menu-item:hover::after) {
  border-bottom-color: #ffb74d !important;
  border-bottom-width: 3px !important;
}

.main-menu :deep(.ant-menu-item-selected) {
  color: #ff8c42;
  background: rgba(255, 140, 66, 0.15);
}

.main-menu :deep(.ant-menu-item-selected::after) {
  border-bottom-color: #ff8c42;
  border-bottom-width: 2px;
}

.main-menu :deep(.ant-menu-item::after) {
  transition: all 0.3s ease;
}

.user-section {
  min-width: 140px;
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
}

.login-btn {
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 100%);
  border: none;
  border-radius: 20px;
  font-weight: 600;
  padding: 0 20px;
  height: 36px;
  box-shadow: 0 2px 8px rgba(255, 140, 66, 0.3);
  transition: all 0.3s ease;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.locale-btn {
  color: #64748b;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  border-radius: 6px;
  padding: 4px 8px;
  height: 32px;
  min-width: 36px;
}

.locale-btn:hover {
  color: #ff8c42;
  border-color: rgba(255, 140, 66, 0.2);
  background: rgba(255, 140, 66, 0.05);
}

.login-btn:hover {
  background: linear-gradient(135deg, #f57c00 0%, #ff9800 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 140, 66, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .logo-section {
    min-width: 150px;
  }

  .site-title {
    font-size: 16px;
  }

  .main-menu {
    display: none; /* 在小屏幕上隐藏菜单，可以后续添加移动端菜单 */
  }
}

@media (max-width: 480px) {
  .logo-section {
    min-width: 120px;
  }

  .site-title {
    display: none; /* 在很小的屏幕上隐藏标题 */
  }
}
</style>
