// 全局类型定义
export interface BaseResponse<T = any> {
  code: number
  message: string
  data: T
  success: boolean
}

// 分页请求参数
export interface PageRequest {
  current: number
  pageSize: number
  sortField?: string
  sortOrder?: 'ascend' | 'descend'
}

// 分页响应结果
export interface PageResponse<T> {
  records: T[]
  total: number
  current: number
  pageSize: number
}

// 通用列表查询参数
export interface ListRequest extends PageRequest {
  keyword?: string
  status?: string
  createTime?: [string, string]
}

// 用户信息
export interface UserInfo {
  id: number
  username: string
  email: string
  avatar?: string
  nickname?: string
  phone?: string
  role: string
  createTime: string
  updateTime: string
}

// 路由元信息 - 已在 router.d.ts 中声明

// 菜单项
export interface MenuItem {
  key: string
  label: string
  icon?: string
  children?: MenuItem[]
}
