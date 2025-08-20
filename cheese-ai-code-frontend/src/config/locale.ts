import type { Locale } from 'ant-design-vue/es/locale-provider'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import enUS from 'ant-design-vue/es/locale/en_US'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import 'dayjs/locale/en'
import { STORAGE_KEYS } from '@/constants'

// 支持的语言类型
export type SupportedLocale = 'zh-CN' | 'en-US'

// 语言配置映射
export const localeConfigs = {
  'zh-CN': {
    antd: zhCN,
    dayjs: 'zh-cn',
    name: '简体中文'
  },
  'en-US': {
    antd: enUS,
    dayjs: 'en',
    name: 'English'
  }
} as const

// 默认语言
export const DEFAULT_LOCALE: SupportedLocale = 'zh-CN'

/**
 * 设置全局语言
 * @param locale 语言代码
 */
export function setGlobalLocale(locale: SupportedLocale): Locale {
  const config = localeConfigs[locale]

  // 设置dayjs语言
  dayjs.locale(config.dayjs)

  // 保存到本地存储
  localStorage.setItem(STORAGE_KEYS.LOCALE, locale)

  return config.antd
}

/**
 * 获取当前语言设置
 */
export function getCurrentLocale(): SupportedLocale {
  const saved = localStorage.getItem(STORAGE_KEYS.LOCALE) as SupportedLocale
  if (saved && saved in localeConfigs) {
    return saved
  }

  // 根据浏览器语言自动检测
  const browserLang = navigator.language
  if (browserLang.startsWith('zh')) {
    return 'zh-CN'
  }

  return DEFAULT_LOCALE
}

/**
 * 获取语言配置
 */
export function getLocaleConfig(locale?: SupportedLocale) {
  const currentLocale = locale || getCurrentLocale()
  return localeConfigs[currentLocale]
}
