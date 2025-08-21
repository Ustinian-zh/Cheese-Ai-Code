import zhCN from 'ant-design-vue/es/locale/zh_CN'
import enUS from 'ant-design-vue/es/locale/en_US'

export const locales = {
  'zh-CN': {
    name: '中文',
    antdLocale: zhCN
  },
  'en-US': {
    name: 'English',
    antdLocale: enUS
  }
}

export type LocaleKey = keyof typeof locales
export type SupportedLocale = LocaleKey

// 当前语言（从localStorage读取或默认中文）
let currentGlobalLocale: SupportedLocale = 'zh-CN'

export function getCurrentLocale(): SupportedLocale {
  if (typeof window !== 'undefined') {
    const stored = localStorage.getItem('locale') as SupportedLocale
    if (stored && stored in locales) {
      currentGlobalLocale = stored
    }
  }
  return currentGlobalLocale
}

export function setGlobalLocale(locale: SupportedLocale) {
  currentGlobalLocale = locale
  if (typeof window !== 'undefined') {
    localStorage.setItem('locale', locale)
  }
}

export function getLocaleConfig(locale: SupportedLocale) {
  return {
    name: locales[locale].name,
    antd: locales[locale].antdLocale
  }
}
