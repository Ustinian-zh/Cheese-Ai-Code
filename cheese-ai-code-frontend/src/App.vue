<script setup lang="ts">
import { ConfigProvider } from 'ant-design-vue'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useLocale } from '@/composables/useLocale'

// 初始化国际化
const { antdLocale } = useLocale()
</script>

<template>
  <ConfigProvider :locale="antdLocale">
    <BasicLayout />
  </ConfigProvider>
</template>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  line-height: 1.5;
  color: #333;
  background-image: url('@/assets/backimg1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

#app {
  height: 100%;
}

/* 链接样式 */
a {
  color: #ff8c42;
  text-decoration: none;
  transition: all 0.3s ease;
}

a:hover {
  color: #f57c00;
}

/* 清除默认的 Ant Design 样式影响 */
.ant-layout {
  background: transparent;
}
</style>
