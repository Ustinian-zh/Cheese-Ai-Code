import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { routeConfigs, generateMenuItems } from '@/config/routes'
import type { MenuItem } from '@/types'

export function useMenu() {
  const router = useRouter()
  const route = useRoute()

  // 当前选中的菜单项
  const selectedKeys = ref<string[]>([route.name as string || 'home'])

  // 从路由配置生成菜单项
  const menuItems = computed(() => generateMenuItems(routeConfigs))

  // 菜单点击处理
  const handleMenuClick = ({ key }: { key: string }) => {
    selectedKeys.value = [key]

    // 根据key导航到对应路由
    router.push({ name: key }).catch(err => {
      console.warn('路由导航失败:', err)
    })
  }

  // 监听路由变化，更新选中状态
  const updateSelectedKeys = () => {
    if (route.name) {
      selectedKeys.value = [route.name as string]
    }
  }

  // 获取当前页面标题
  const getCurrentPageTitle = computed(() => {
    const currentRoute = routeConfigs.find(config => config.name === route.name)
    return currentRoute?.meta.title || '奶酪AI代码平台'
  })

  return {
    menuItems,
    selectedKeys,
    handleMenuClick,
    updateSelectedKeys,
    getCurrentPageTitle
  }
}
