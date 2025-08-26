/**
 * 代码生成类型枚举与工具（与后端 CodeGenTypeEnum 对齐）
 */

export enum CodeGenTypeEnum {
  HTML = 'html',
  MULTI_FILE = 'multi_file',
}

export const CODE_GEN_TYPE_CONFIG = {
  [CodeGenTypeEnum.HTML]: {
    label: '原生 HTML 模式',
    value: CodeGenTypeEnum.HTML,
  },
  [CodeGenTypeEnum.MULTI_FILE]: {
    label: '原生多文件模式',
    value: CodeGenTypeEnum.MULTI_FILE,
  },
} as const

export const CODE_GEN_TYPE_OPTIONS = Object.values(CODE_GEN_TYPE_CONFIG).map((config) => ({
  label: config.label,
  value: config.value,
}))

/**
 * 规范化后端返回或用户选择的生成类型字符串
 * - 忽略大小写
 * - 支持 "HTML" / "MULTI_FILE" / "multiFile" 等变体
 */
export const normalizeCodeGenType = (type: string | undefined | null): string | undefined => {
  if (!type) return undefined
  const raw = String(type).trim()
  if (!raw) return undefined
  const lower = raw.toLowerCase()
  const alnum = lower.replace(/[^a-z0-9]/g, '')

  // 广义匹配：只要包含 html 关键字，视为单 HTML 模式
  if (alnum.includes('html') || alnum.includes('singlehtml') || alnum.includes('rawhtml')) {
    return CodeGenTypeEnum.HTML
  }
  // 广义匹配：multi_file / multifile / multi-file / multiFiles 等
  if (
    alnum.includes('multifile') ||
    alnum.includes('multifiles') ||
    alnum.includes('multi_file') ||
    alnum.includes('multifilemode') ||
    alnum.includes('multifileproject')
  ) {
    return CodeGenTypeEnum.MULTI_FILE
  }
  // 兼容枚举常量名
  if (raw === 'HTML') return CodeGenTypeEnum.HTML
  if (raw === 'MULTI_FILE') return CodeGenTypeEnum.MULTI_FILE
  return lower
}

export const formatCodeGenType = (type: string | undefined): string => {
  const norm = normalizeCodeGenType(type)
  if (!norm) return '未知类型'
  const config = CODE_GEN_TYPE_CONFIG[norm as CodeGenTypeEnum]
  return config ? config.label : (type ?? '未知类型')
}

export const getAllCodeGenTypes = () => {
  return Object.values(CodeGenTypeEnum)
}

export const isValidCodeGenType = (type: string): type is CodeGenTypeEnum => {
  return Object.values(CodeGenTypeEnum).includes(type as CodeGenTypeEnum)
}





