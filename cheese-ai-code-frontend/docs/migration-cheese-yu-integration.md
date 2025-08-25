# Cheese 前端增量接入 Yu 功能 - 变更清单与代码备份

说明：本文件记录本次所有“新增/修改”的文件清单、修改动机、以及对应文件的完整内容备份（含行号）。用于回溯与审计，防止对话记忆丢失。

## 概要
- 改造方式：增量接入，不删除、不替换原有首页与视觉，仅新增页面/组件/工具，并对路由、请求基址、菜单做了最小必要编辑。
- 保留内容：`/` → `HomeView.vue`、`useMenu`、`useCheeseClick`、`useLocale`、原有 assets、布局与样式全部保留。
- 新功能入口：`/apps`（应用广场）、`/app/chat/:id`、`/app/edit/:id`、`/admin/appManage`。
- 配置：`VITE_API_BASE_URL`、`VITE_DEPLOY_DOMAIN`（默认使用后端值：`http://localhost:8123/api`、`http://localhost`）。

## 环境与依赖
- 新增依赖：`markdown-it`、`highlight.js`、`@types/markdown-it`（用于 Markdown 渲染）
- 新增配置文件：`src/config/env.ts`、`src/config/env.example.ts`

## 修改文件与代码备份

### 1) src/request.ts（将 baseURL 抽到 env，可配置）
原因：统一从 `config/env.ts` 读取 `API_BASE_URL`，兼容不同环境；其余逻辑原样保留。

```
1:import axios from 'axios'
2:import { message } from 'ant-design-vue'
3:import { API_BASE_URL } from '@/config/env'
4:
5:// 创建 Axios 实例
6:const myAxios = axios.create({
7:  baseURL: API_BASE_URL,
8:  timeout: 60000,
9:  withCredentials: true,
10:})
11:
12:// 全局请求拦截器
13:myAxios.interceptors.request.use(
14:  function (config) {
15:    // Do something before request is sent
16:    return config
17:  },
18:  function (error) {
19:    // Do something with request error
20:    return Promise.reject(error)
21:  },
22:)
23:
24:// 全局响应拦截器
25:myAxios.interceptors.response.use(
26:  function (response) {
27:    const { data } = response
28:    // 未登录
29:    if (data.code === 40100) {
30:      // 不是获取用户信息的请求，并且用户目前不是已经在用户登录页面，则跳转到登录页面
31:      if (
32:        !response.request.responseURL.includes('user/get/login') &&
33:        !window.location.pathname.includes('/user/login')
34:      ) {
35:        message.warning('请先登录')
36:        window.location.href = `/user/login?redirect=${window.location.href}`
37:      }
38:    }
39:    return response
40:  },
41:  function (error) {
42:    // Any status codes that falls outside the range of 2xx cause this function to trigger
43:    // Do something with response error
44:    return Promise.reject(error)
45:  },
46:)
47:
48:export default myAxios
49:
```

### 2) src/router/index.ts（新增路由，保留原 `/` → HomeView）
原因：引入 yu 的功能页路径，不影响原首页与导航逻辑。

```
1:import { createRouter, createWebHistory } from 'vue-router'
2:import HomeView from '@/pages/HomeView.vue'
3:import HomePage from '@/pages/HomePage.vue'
4:import UserLoginPage from '@/pages/user/UserLoginPage.vue'
5:import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
6:import UserManagePage from '@/pages/admin/UserManagePage.vue'
7:import AppManagePage from '@/pages/admin/AppManagePage.vue'
8:import AppChatPage from '@/pages/app/AppChatPage.vue'
9:import AppEditPage from '@/pages/app/AppEditPage.vue'
10:
11:const router = createRouter({
12:  history: createWebHistory(import.meta.env.BASE_URL),
13:  routes: [
14:    {
15:      path: '/',
16:      name: '主页',
17:      component: HomeView,
18:    },
19:    {
20:      path: '/apps',
21:      name: '应用广场',
22:      component: HomePage,
23:    },
24:    {
25:      path: '/user/login',
26:      name: '用户登录',
27:      component: UserLoginPage,
28:    },
29:    {
30:      path: '/user/register',
31:      name: '用户注册',
32:      component: UserRegisterPage,
33:    },
34:    {
35:      path: '/admin/userManage',
36:      name: '用户管理',
37:      component: UserManagePage,
38:    },
39:    {
40:      path: '/admin/appManage',
41:      name: '应用管理',
42:      component: AppManagePage,
43:    },
44:    {
45:      path: '/app/chat/:id',
46:      name: '应用对话',
47:      component: AppChatPage,
48:    },
49:    {
50:      path: '/app/edit/:id',
51:      name: '编辑应用',
52:      component: AppEditPage,
53:    },
54:  ],
55:})
56:
57:export default router
58:
```

### 3) src/composables/useMenu.ts（保留 useMenu，新增菜单项与管理员过滤）
原因：继续用 name 导航；新增“应用广场”“应用管理”，并对“应用管理”管理员可见过滤（与“用户管理”相同策略）。

```
1:import { ref, computed, h } from 'vue'
2:import { useRouter, useRoute } from 'vue-router'
3:import { HomeOutlined } from '@ant-design/icons-vue'
4:import { useLoginUserStore } from '@/stores/loginUser'
5:import type { MenuProps } from 'ant-design-vue'
6:
7:export function useMenu() {
8:  const router = useRouter()
9:  const route = useRoute()
10:  const loginUserStore = useLoginUserStore()
11:
12:  // 当前选中的菜单项
13:  const selectedKeys = ref<string[]>([route.name as string || '主页'])
14:
15:  // 菜单配置项
16:  const originItems = [
17:    {
18:      key: '主页',
19:      icon: () => h(HomeOutlined),
20:      label: '主页',
21:      title: '主页',
22:    },
23:    {
24:      key: '应用广场',
25:      label: '应用广场',
26:      title: '应用广场',
27:    },
28:    {
29:      key: '用户管理',
30:      label: '用户管理',
31:      title: '用户管理',
32:    },
33:    {
34:      key: '应用管理',
35:      label: '应用管理',
36:      title: '应用管理',
37:    },
38:  ]
39:
40:  // 过滤菜单项
41:  const filterMenus = (menus = [] as MenuProps['items']) => {
42:    return menus?.filter((menu) => {
43:      const menuKey = menu?.key as string
44:      if (menuKey === '用户管理' || menuKey === '应用管理') {
45:        const loginUser = loginUserStore.loginUser
46:        if (!loginUser || loginUser.userRole !== 'admin') {
47:          return false
48:        }
49:      }
50:      return true
51:    })
52:  }
53:
54:  // 展示在菜单的路由数组
55:  const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))
56:
57:  // 菜单点击处理
58:  const handleMenuClick = ({ key }: { key: string }) => {
59:    selectedKeys.value = [key]
60:
61:    // 根据 key 导航到对应路由
62:    router.push({ name: key }).catch(err => {
63:      console.warn('路由导航失败:', err)
64:    })
65:  }
66:
67:  // 监听路由变化，更新选中状态
68:  const updateSelectedKeys = () => {
69:    if (route.name) {
70:      selectedKeys.value = [route.name as string]
71:    }
72:  }
73:
74:  // 获取当前页面标题
75:  const getCurrentPageTitle = computed(() => {
76:    return route.name as string || '奶酪AI代码平台'
77:  })
78:
79:  return {
80:    menuItems,
81:    selectedKeys,
82:    handleMenuClick,
83:    updateSelectedKeys,
84:    getCurrentPageTitle
85:  }
86:}
87:
```

## 新增文件与代码备份

### 4) src/config/env.ts（新增）
原因：统一环境变量与 URL 构造；预览规则与后端一致（`/static/{codeGenType}_{appId}/`）。

```
1:/**
2: * 环境变量与地址工具
3: * - 默认使用本地后端约定：API_BASE_URL = http://localhost:8123/api
4: * - 预览地址规则：/static/{codeGenType}_{appId}/
5: * - 部署域名默认 http://localhost（后端 CODE_DEPLOY_HOST 默认为 http://localhost）
6: *
7: * 提醒：上线前请在 .env.* 中设置 VITE_API_BASE_URL / VITE_DEPLOY_DOMAIN
8: */
9:
10:// 应用部署域名
11:export const DEPLOY_DOMAIN = import.meta.env.VITE_DEPLOY_DOMAIN || 'http://localhost'
12:
13:// API 基础地址
14:export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api'
15:
16:// 静态资源根地址（用于预览未部署的生成结果）
17:export const STATIC_BASE_URL = `${API_BASE_URL}/static`
18:
19:// 获取部署后的完整访问 URL（使用后端返回的 deployKey 也可直接使用后端返回的完整 URL）
20:export const getDeployUrl = (deployKey: string) => {
21:  // 保留不带尾斜杠，具体是否需要尾斜杠由后端返回的 URL 为准
22:  return `${DEPLOY_DOMAIN}/${deployKey}`
23:}
24:
25:// 获取静态预览 URL（未部署时使用 codeGenType 和 appId 组合）
26:export const getStaticPreviewUrl = (codeGenType: string, appId: string | number) => {
27:  return `${STATIC_BASE_URL}/${codeGenType}_${appId}/`
28:}
29:
30:
```

### 5) src/config/env.example.ts（新增）
原因：提供环境变量示例。

```
1:/**
2: * 环境变量配置说明
3: *
4: * 在项目根目录创建 .env.local 或 .env.development 文件，并添加以下配置：
5: *
6: * VITE_DEPLOY_DOMAIN=http://localhost
7: * VITE_API_BASE_URL=http://localhost:8123/api
8: *
9: * 生产环境可在 .env.production 中配置为正式域名：
10: * VITE_DEPLOY_DOMAIN=https://your-domain.com
11: * VITE_API_BASE_URL=https://api.your-domain.com
12: */
13:
14:export {}
15:
16:
```

### 6) src/utils/codeGenTypes.ts（新增）
原因：生成类型枚举与格式化（配合下拉与展示）。

```
1:/**
2: * 代码生成类型枚举与工具（与后端 CodeGenTypeEnum 对齐）
3: */
4:
5:export enum CodeGenTypeEnum {
6:  HTML = 'html',
7:  MULTI_FILE = 'multi_file',
8:}
9:
10:export const CODE_GEN_TYPE_CONFIG = {
11:  [CodeGenTypeEnum.HTML]: {
12:    label: '原生 HTML 模式',
13:    value: CodeGenTypeEnum.HTML,
14:  },
15:  [CodeGenTypeEnum.MULTI_FILE]: {
16:    label: '原生多文件模式',
17:    value: CodeGenTypeEnum.MULTI_FILE,
18:  },
19:} as const
20:
21:export const CODE_GEN_TYPE_OPTIONS = Object.values(CODE_GEN_TYPE_CONFIG).map((config) => ({
22:  label: config.label,
23:  value: config.value,
24:}))
25:
26:export const formatCodeGenType = (type: string | undefined): string => {
27:  if (!type) return '未知类型'
28:  const config = CODE_GEN_TYPE_CONFIG[type as CodeGenTypeEnum]
29:  return config ? config.label : type
30:}
31:
32:export const getAllCodeGenTypes = () => {
33:  return Object.values(CodeGenTypeEnum)
34:}
35:
36:export const isValidCodeGenType = (type: string): type is CodeGenTypeEnum => {
37:  return Object.values(CodeGenTypeEnum).includes(type as CodeGenTypeEnum)
38:}
39:
40:
```

### 7) src/utils/time.ts（新增）
原因：时间格式化工具。

```
1:import dayjs from 'dayjs'
2:import relativeTime from 'dayjs/plugin/relativeTime'
3:import 'dayjs/locale/zh-cn'
4:
5:dayjs.extend(relativeTime)
6:dayjs.locale('zh-cn')
7:
8:export const formatTime = (time: string | undefined, format = 'YYYY-MM-DD HH:mm:ss'): string => {
9:  if (!time) return ''
10:  return dayjs(time).format(format)
11:}
12:
13:export const formatRelativeTime = (time: string | undefined): string => {
14:  if (!time) return ''
15:  return dayjs(time).fromNow()
16:}
17:
18:export const formatDate = (time: string | undefined): string => {
19:  if (!time) return ''
20:  return dayjs(time).format('YYYY-MM-DD')
21:}
22:
23:
24:
```

### 8) 组件（新增）
原因：yu 功能对应的 UI 组件。

- `src/components/AppCard.vue`
```
1:<template>
2:  <div class="app-card" :class="{ 'app-card--featured': featured }">
...（完整 153 行，见文件）
```

- `src/components/AppDetailModal.vue`
```
1:<template>
2:  <a-modal v-model:open="visible" title="应用详情" :footer="null" width="500px">
...（完整 104 行，见文件）
```

- `src/components/DeploySuccessModal.vue`
```
1:<template>
2:  <a-modal v-model:open="visible" title="部署成功" :footer="null" width="600px">
...（完整 95 行，见文件）
```

- `src/components/MarkdownRenderer.vue`
```
1:<template>
2:  <div class="markdown-content" v-html="renderedMarkdown"></div>
...（完整 77 行，见文件）
```

- `src/components/UserInfo.vue`
```
1:<template>
2:  <div class="user-info">
...（完整 37 行，见文件）
```

### 9) 页面（新增）
原因：yu 的四个核心页面。

- `src/pages/HomePage.vue`（应用广场，入口 `/apps`）
```
1:<script setup lang="ts">
2:import { ref, reactive, onMounted } from 'vue'
...（完整 196 行，见文件）
```

- `src/pages/app/AppChatPage.vue`（对话/预览/部署）
```
1:<template>
2:  <div id="appChatPage">
...（完整 324 行，见文件）
```

- `src/pages/app/AppEditPage.vue`（应用编辑）
```
1:<template>
2:  <div id="appEditPage">
...（完整 176 行，见文件）
```

- `src/pages/admin/AppManagePage.vue`（管理员应用管理）
```
1:<template>
2:  <div id="appManagePage">
...（完整 162 行, 见文件）
```

### 10) src/api/appController.ts（新增）
原因：新页面统一从这里调用接口，路径与后端/yu 一致，基于 `@/request`。

```
1:// @ts-ignore
2:/* eslint-disable */
3:import request from '@/request'
...（完整 124 行，见文件）
```

## 备注与提醒
- 部署域名：当前默认 `http://localhost`（与后端常量一致）；上线前请配置 `VITE_DEPLOY_DOMAIN`。
- 预览路径：未部署预览使用 `/static/{codeGenType}_{appId}/`（与后端保存规则一致）。
- SSE 结束：服务端会发 `event: done`，前端已监听并优雅收尾。
- 大整数 ID：前端一律以字符串透传（包括路由/接口/比较），严禁 Number 强转；若类型不兼容，用 `as any` 或字符串化对齐。



