<template>
  <a-layout class="basic-layout">
    <!-- 顶部导航栏 -->
    <GlobalHeader />

    <!-- 主体内容区域 -->
    <a-layout-content class="content">
      <div class="content-wrapper">
        <RouterView />
      </div>
    </a-layout-content>

    <!-- 底部版权信息 -->
    <GlobalFooter />
  </a-layout>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
import { useCheeseClick } from '@/composables/useCheeseClick'

// 启用奶酪点击特效
useCheeseClick()
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: transparent;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  width: 100%;
  flex: 1;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin-top: 24px;
  margin-bottom: 24px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-wrapper {
    margin: 16px;
    max-width: none;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    margin: 12px;
    padding: 16px;
    border-radius: 4px;
  }
}

@media (max-width: 480px) {
  .content-wrapper {
    margin: 8px;
    padding: 12px;
    margin-top: 12px;
    margin-bottom: 12px;
  }
}
</style>
