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
      <div class="user-login-status">
        <div v-if="loginUserStore.loginUser.id">
          <a-dropdown>
            <a-space>
              <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              {{ loginUserStore.loginUser.userName ?? '无名' }}
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else>
          <a-button type="primary" @click="goToLogin">登录</a-button>
        </div>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { LogoutOutlined } from '@ant-design/icons-vue'
import { useMenu } from '@/composables/useMenu'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()
const { menuItems, selectedKeys, handleMenuClick, updateSelectedKeys } = useMenu()

// 跳转到登录页
const goToLogin = () => {
  router.push('/user/login')
}

// 用户注销
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
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
  height: 64px; /* 确保与header高度一致 */
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
  line-height: 1.2; /* 确保文字行高与logo对齐 */
  display: flex;
  align-items: center;
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

.user-login-status {
  min-width: 140px;
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
}

/* 优化右上角登录按钮 */
.user-login-status :deep(.ant-btn-primary) {
  height: 40px;
  border-radius: 20px;
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 100%);
  border: none;
  font-size: 14px;
  font-weight: 600;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(255, 140, 66, 0.3);
  transition: all 0.3s ease;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.user-login-status :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #f57c00 0%, #ff9800 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 140, 66, 0.4);
}

.user-login-status :deep(.ant-btn-primary:active) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(255, 140, 66, 0.3);
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
