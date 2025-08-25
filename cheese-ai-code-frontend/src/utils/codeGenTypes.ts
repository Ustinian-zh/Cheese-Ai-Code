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

export const formatCodeGenType = (type: string | undefined): string => {
  if (!type) return '未知类型'
  const config = CODE_GEN_TYPE_CONFIG[type as CodeGenTypeEnum]
  return config ? config.label : type
}

export const getAllCodeGenTypes = () => {
  return Object.values(CodeGenTypeEnum)
}

export const isValidCodeGenType = (type: string): type is CodeGenTypeEnum => {
  return Object.values(CodeGenTypeEnum).includes(type as CodeGenTypeEnum)
}



