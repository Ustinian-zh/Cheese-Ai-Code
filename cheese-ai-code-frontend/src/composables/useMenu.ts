import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { HomeOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import type { MenuProps } from 'ant-design-vue'

export function useMenu() {
  const router = useRouter()
  const route = useRoute()
  const loginUserStore = useLoginUserStore()

  // 当前选中的菜单项
  const selectedKeys = ref<string[]>([route.name as string || '主页'])

  // 菜单配置项
  const originItems = [
    {
      key: '主页',
      icon: () => h(HomeOutlined),
      label: '主页',
      title: '主页',
    },
    {
      key: '应用广场',
      label: '应用广场',
      title: '应用广场',
    },
    {
      key: '用户管理',
      label: '用户管理',
      title: '用户管理',
    },
    {
      key: '应用管理',
      label: '应用管理',
      title: '应用管理',
    },
    {
      key: '对话管理',
      label: '对话管理',
      title: '对话管理',
    },
  ]

  // 过滤菜单项
  const filterMenus = (menus = [] as MenuProps['items']) => {
    return menus?.filter((menu) => {
      const menuKey = menu?.key as string
      if (menuKey === '用户管理' || menuKey === '应用管理' || menuKey === '对话管理') {
        const loginUser = loginUserStore.loginUser
        if (!loginUser || loginUser.userRole !== 'admin') {
          return false
        }
      }
      return true
    })
  }

  // 展示在菜单的路由数组
  const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

  // 菜单点击处理
  const handleMenuClick = ({ key }: { key: string }) => {
    selectedKeys.value = [key]

    // 根据 key 导航到对应路由
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
    return route.name as string || '奶酪AI代码平台'
  })

  return {
    menuItems,
    selectedKeys,
    handleMenuClick,
    updateSelectedKeys,
    getCurrentPageTitle
  }
}
