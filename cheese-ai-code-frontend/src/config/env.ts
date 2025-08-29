/**
 * 环境变量与地址工具
 * - 默认使用本地后端约定：API_BASE_URL = http://localhost:8123/api
 * - 预览地址规则：/static/{codeGenType}_{appId}/
 * - 部署域名默认 http://localhost（后端 CODE_DEPLOY_HOST 默认为 http://localhost）
 *
 * 提醒：上线前请在 .env.* 中设置 VITE_API_BASE_URL / VITE_DEPLOY_DOMAIN
 */

// 应用部署域名
export const DEPLOY_DOMAIN = import.meta.env.VITE_DEPLOY_DOMAIN || 'http://localhost'

// API 基础地址
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api'

// 静态资源根地址（用于预览未部署的生成结果）
export const STATIC_BASE_URL = `${API_BASE_URL}/static`

// 获取部署后的完整访问 URL（使用后端返回的 deployKey 也可直接使用后端返回的完整 URL）
export const getDeployUrl = (deployKey: string) => {
  // 保留不带尾斜杠，具体是否需要尾斜杠由后端返回的 URL 为准
  return `${DEPLOY_DOMAIN}/${deployKey}`
}

// 获取静态预览 URL（未部署时使用 codeGenType 和 appId 组合）
export const getStaticPreviewUrl = (codeGenType: string, appId: string | number) => {
  const baseUrl = `${STATIC_BASE_URL}/${codeGenType}_${appId}/`
  // Vue 工程模式：浏览地址需要添加 dist/index.html
  if (String(codeGenType).toLowerCase() === 'vue_project') {
    return `${baseUrl}dist/index.html`
  }
  return baseUrl
}





