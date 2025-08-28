<script setup lang="ts">
import { ConfigProvider } from 'ant-design-vue'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useLocale } from '@/composables/useLocale'

// 初始化国际化
const { antdLocale } = useLocale()

// 全局主题（橙色主色，圆角 8，不改尺寸）
const theme = {
  token: {
    colorPrimary: '#ff8c42',
    colorInfo: '#ff8c42',
    colorLink: '#ff8c42',
    colorLinkHover: '#f57c00',
    colorLinkActive: '#e56a00',
    borderRadius: 8,
  },
  components: {
    Button: {
      colorPrimaryHover: '#f57c00',
      colorPrimaryActive: '#e56a00',
    },
  },
}
</script>

<template>
  <ConfigProvider :locale="antdLocale" :theme="theme">
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

/* 方案 1：主按钮橙色轻渐变，hover 稍深；不改尺寸 */
:root {}
.ant-btn-primary {
  background: linear-gradient(135deg, #ff9d5f 0%, #ff8c42 60%, #f57c00 100%);
  border: none;
}
.ant-btn-primary:hover,
.ant-btn-primary:focus {
  background: linear-gradient(135deg, #ff933f 0%, #f57c00 60%, #e56a00 100%);
}
.ant-btn-primary:active {
  background: linear-gradient(135deg, #f57c00 0%, #e56a00 60%, #cf5f00 100%);
}

/* 默认按钮浅灰边框，圆角随 token */
.ant-btn-default {
  border-color: #e5e7eb;
  color: #334155;
}
.ant-btn-default:hover,
.ant-btn-default:focus {
  border-color: #d1d5db;
  color: #1f2937;
}
</style>
