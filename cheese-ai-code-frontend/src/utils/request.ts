import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'
import { message } from 'ant-design-vue'
import { API_CODE, STORAGE_KEYS } from '@/constants'

// API响应数据结构接口
export interface ApiResponse<T = any> {
  code: number
  data: T
  message: string
  success: boolean
}

// 请求配置接口
export interface RequestConfig extends AxiosRequestConfig {
  skipErrorHandler?: boolean // 是否跳过错误处理
  skipAuth?: boolean // 是否跳过认证检查
}

// 环境配置
const getBaseURL = (): string => {
  const { NODE_ENV } = import.meta.env

  switch (NODE_ENV) {
    case 'development':
      return import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api'
    case 'production':
      return import.meta.env.VITE_API_BASE_URL || 'https://api.cheese-ai.com'
    default:
      return 'http://localhost:8123/api'
  }
}

// 创建 Axios 实例
const myAxios: AxiosInstance = axios.create({
  baseURL: getBaseURL(),
  timeout: 60000, // 60秒超时
  withCredentials: true, // 携带Cookie，用于用户认证
  headers: {
    'Content-Type': 'application/json',
  }
})

// 全局请求拦截器
myAxios.interceptors.request.use(
  function (config: any) {
    // 在请求发送之前做一些处理

    // 添加时间戳防止缓存（可选）
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }

    // 添加请求头（如token等）
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN)
    if (token && !config.skipAuth) {
      config.headers = {
        ...config.headers,
        'Authorization': `Bearer ${token}`
      }
    }

    // 开发环境下打印请求信息
    if (import.meta.env.DEV) {
      console.log('🚀 Request:', {
        url: config.url,
        method: config.method,
        params: config.params,
        data: config.data
      })
    }

    return config
  },
  function (error: AxiosError) {
    // 请求错误处理
    console.error('❌ Request Error:', error)
    message.error('请求发送失败')
    return Promise.reject(error)
  }
)

// 全局响应拦截器
myAxios.interceptors.response.use(
  function (response: AxiosResponse<ApiResponse>) {
    const { data, config } = response

    // 开发环境下打印响应信息
    if (import.meta.env.DEV) {
      console.log('📦 Response:', {
        url: response.config.url,
        status: response.status,
        data: data
      })
    }

    // 如果配置了跳过错误处理，直接返回
    if ((config as RequestConfig).skipErrorHandler) {
      return response
    }

        // 处理业务状态码
    if (data && typeof data === 'object' && 'code' in data) {
      switch (data.code) {
        case API_CODE.SUCCESS: // 成功
          return response

        case API_CODE.UNAUTHORIZED: // 未登录
          handleUnauthorized(response)
          return Promise.reject(new Error(data.message || '未登录'))

        case API_CODE.FORBIDDEN: // 权限不足
          message.error(data.message || '权限不足')
          return Promise.reject(new Error(data.message || '权限不足'))

        case API_CODE.BAD_REQUEST: // 请求参数错误
          message.error(data.message || '请求参数错误')
          return Promise.reject(new Error(data.message || '请求参数错误'))

        case API_CODE.SERVER_ERROR: // 服务器内部错误
          message.error(data.message || '服务器内部错误')
          return Promise.reject(new Error(data.message || '服务器内部错误'))

        default: // 其他错误
          message.error(data.message || '未知错误')
          return Promise.reject(new Error(data.message || '未知错误'))
      }
    }

    return response
  },
  function (error: AxiosError) {
    // HTTP状态码错误处理
    const { response, config } = error

    // 如果配置了跳过错误处理，直接返回
    if ((config as RequestConfig)?.skipErrorHandler) {
      return Promise.reject(error)
    }

    // 开发环境下打印错误信息
    if (import.meta.env.DEV) {
      console.error('❌ Response Error:', error)
    }

    if (response) {
      switch (response.status) {
        case 401:
          handleUnauthorized(response)
          break
        case 403:
          message.error('权限不足')
          break
        case 404:
          message.error('请求的资源不存在')
          break
        case 500:
          message.error('服务器内部错误')
          break
        case 502:
          message.error('网关错误')
          break
        case 503:
          message.error('服务不可用')
          break
        case 504:
          message.error('网关超时')
          break
        default:
          message.error(`请求失败 (${response.status})`)
      }
    } else if (error.request) {
      // 网络错误
      message.error('网络连接失败，请检查网络')
    } else {
      // 其他错误
      message.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

// 处理未登录状态
function handleUnauthorized(response: AxiosResponse) {
  // 不是获取用户信息的请求，并且用户目前不是已经在用户登录页面，则跳转到登录页面
  if (
    !response.request.responseURL.includes('user/get/login') &&
    !window.location.pathname.includes('/user/login')
  ) {
    message.warning('登录已过期，请重新登录')

    // 清除本地存储的用户信息
    localStorage.removeItem(STORAGE_KEYS.TOKEN)
    localStorage.removeItem(STORAGE_KEYS.USER_INFO)

    // 跳转到登录页面，并携带当前页面地址
    const currentPath = window.location.pathname + window.location.search
    const loginUrl = `/user/login?redirect=${encodeURIComponent(currentPath)}`

    // 延迟跳转，给用户看到提示信息的时间
    setTimeout(() => {
      window.location.href = loginUrl
    }, 1500)
  }
}

// 封装常用的请求方法
export const requestUtils = {
  // GET请求
  get<T = any>(url: string, params?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return myAxios.get(url, { ...config, params }).then(res => res.data)
  },

  // POST请求
  post<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return myAxios.post(url, data, config).then(res => res.data)
  },

  // PUT请求
  put<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return myAxios.put(url, data, config).then(res => res.data)
  },

  // DELETE请求
  delete<T = any>(url: string, config?: RequestConfig): Promise<ApiResponse<T>> {
    return myAxios.delete(url, config).then(res => res.data)
  },

  // PATCH请求
  patch<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return myAxios.patch(url, data, config).then(res => res.data)
  },

  // 上传文件
  upload<T = any>(url: string, file: File, onProgress?: (progress: number) => void): Promise<ApiResponse<T>> {
    const formData = new FormData()
    formData.append('file', file)

    return myAxios.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(progress)
        }
      }
    }).then(res => res.data)
  }
}

// 为OpenAPI生成的代码提供的请求函数
// 这个函数签名与OpenAPI工具期望的格式一致
export default function request<T = any>(
  url: string,
  options?: RequestConfig & {
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
    params?: any
    data?: any
  }
): Promise<ApiResponse<T>> {
  const { method = 'GET', params, data, ...config } = options || {}

  switch (method.toUpperCase()) {
    case 'GET':
      return myAxios.get(url, { ...config, params }).then(res => res.data)
    case 'POST':
      return myAxios.post(url, data, config).then(res => res.data)
    case 'PUT':
      return myAxios.put(url, data, config).then(res => res.data)
    case 'DELETE':
      return myAxios.delete(url, config).then(res => res.data)
    case 'PATCH':
      return myAxios.patch(url, data, config).then(res => res.data)
    default:
      return myAxios.request({ url, method, params, data, ...config }).then(res => res.data)
  }
}

// 导出axios实例供特殊需求使用
export { myAxios }

// 为了向后兼容，保留 request 别名
export { requestUtils as request }
