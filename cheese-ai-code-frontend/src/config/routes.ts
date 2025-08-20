import type { RouteRecordRaw, RouteMeta } from 'vue-router'
import type { MenuItem } from '@/types'
import { ROUTES } from '@/constants'

// 路由配置接口
export interface RouteConfig {
  path: string
  name: string
  component: () => Promise<any>
  meta: RouteMeta
  children?: RouteConfig[]
}

// 路由配置数组 - 保留核心功能页面
export const routeConfigs: RouteConfig[] = [
  {
    path: ROUTES.HOME,
    name: 'home',
    component: () => import('@/pages/HomeView.vue'),
    meta: {
      title: '首页',
      icon: 'home',
      order: 1
    }
  },
  {
    path: ROUTES.FEATURES,
    name: 'features',
    component: () => import('@/pages/FeaturesView.vue'),
    meta: {
      title: '功能特性',
      icon: 'appstore',
      order: 2
    }
  },
  {
    path: ROUTES.ABOUT,
    name: 'about',
    component: () => import('@/pages/AboutView.vue'),
    meta: {
      title: '关于我们',
      icon: 'team',
      order: 3
    }
  },
  {
    path: ROUTES.CONTACT,
    name: 'contact',
    component: () => import('@/pages/ContactView.vue'),
    meta: {
      title: '联系我们',
      icon: 'phone',
      order: 4
    }
  }
]

// 将配置转换为Vue Router格式
export function transformToRoutes(configs: RouteConfig[]): RouteRecordRaw[] {
  return configs.map(config => {
    const route: RouteRecordRaw = {
      path: config.path,
      name: config.name,
      component: config.component,
      meta: config.meta,
      ...(config.children && { children: transformToRoutes(config.children) })
    }

    return route
  })
}

// 生成菜单项
export function generateMenuItems(configs: RouteConfig[]): MenuItem[] {
  return configs
    .filter(config => !config.meta.hidden) // 过滤隐藏的菜单项
    .sort((a, b) => (a.meta.order || 0) - (b.meta.order || 0)) // 按order排序
    .map(config => ({
      key: config.name,
      label: config.meta.title,
      icon: config.meta.icon,
      children: config.children ? generateMenuItems(config.children) : undefined
    }))
}

// 根据路由名称获取菜单标题
export function getRouteTitle(routeName: string): string {
  const findRoute = (configs: RouteConfig[]): RouteConfig | undefined => {
    for (const config of configs) {
      if (config.name === routeName) {
        return config
      }
      if (config.children) {
        const found = findRoute(config.children)
        if (found) return found
      }
    }
    return undefined
  }

  const route = findRoute(routeConfigs)
  return route?.meta.title || routeName
}
