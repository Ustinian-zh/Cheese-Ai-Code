// 扩展Vue Router的类型定义
import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title: string
    icon?: string
    hidden?: boolean
    auth?: string[]
    order?: number
  }
}
