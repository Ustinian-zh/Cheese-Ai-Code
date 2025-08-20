import { createRouter, createWebHistory } from 'vue-router'
import { routeConfigs, transformToRoutes, getRouteTitle } from '@/config/routes'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: transformToRoutes(routeConfigs),
})

// 全局路由守卫：设置页面标题
router.beforeEach((to, from, next) => {
  const title = getRouteTitle(to.name as string)
  document.title = title ? `${title} - 奶酪AI代码平台` : '奶酪AI代码平台'
  next()
})

export default router
