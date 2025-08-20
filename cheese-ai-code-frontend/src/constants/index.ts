// 应用常量定义

// API响应状态码
export const API_CODE = {
  SUCCESS: 0,
  UNAUTHORIZED: 40100,
  FORBIDDEN: 40101,
  BAD_REQUEST: 40400,
  SERVER_ERROR: 50000,
} as const

// 本地存储键名
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_INFO: 'userInfo',
  LOCALE: 'locale',
  THEME: 'theme',
} as const

// 路由路径
export const ROUTES = {
  HOME: '/',
  LOGIN: '/user/login',
  FEATURES: '/features',
  ABOUT: '/about',
  CONTACT: '/contact',
} as const

// 默认配置
export const DEFAULT_CONFIG = {
  PAGE_SIZE: 10,
  TIMEOUT: 60000,
  RETRY_COUNT: 3,
} as const

// 环境变量
export const ENV = {
  DEV: 'development',
  PROD: 'production',
  TEST: 'test',
} as const
