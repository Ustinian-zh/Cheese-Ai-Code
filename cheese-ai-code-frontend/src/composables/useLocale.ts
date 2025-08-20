import { ref, computed } from 'vue'
import type { Locale } from 'ant-design-vue/es/locale-provider'
import {
  setGlobalLocale,
  getCurrentLocale,
  getLocaleConfig,
  type SupportedLocale
} from '@/config/locale'

// 当前语言状态
const currentLocale = ref<SupportedLocale>(getCurrentLocale())

/**
 * 国际化状态管理
 */
export function useLocale() {
  // 当前Ant Design语言包
  const antdLocale = computed<Locale>(() => {
    return getLocaleConfig(currentLocale.value).antd
  })

  // 当前语言名称
  const localeName = computed(() => {
    return getLocaleConfig(currentLocale.value).name
  })

  // 是否为中文
  const isZhCN = computed(() => currentLocale.value === 'zh-CN')

  /**
   * 切换语言
   */
  const setLocale = (locale: SupportedLocale) => {
    currentLocale.value = locale
    setGlobalLocale(locale)
  }

  /**
   * 切换中英文
   */
  const toggleLocale = () => {
    const newLocale: SupportedLocale = currentLocale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
    setLocale(newLocale)
  }

  return {
    currentLocale: computed(() => currentLocale.value),
    antdLocale,
    localeName,
    isZhCN,
    setLocale,
    toggleLocale
  }
}
