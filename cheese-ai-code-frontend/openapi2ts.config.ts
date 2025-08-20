export default {
  // 请求库路径，指向我们的request配置
  requestLibPath: "import request from '@/utils/request'",

  // Swagger接口文档地址
  // 开发环境使用本地mock数据，生产环境使用实际API文档
  schemaPath: process.env.NODE_ENV === 'development'
    ? './mock/swagger.json'
    : 'http://localhost:8123/api/v3/api-docs',

  // 生成代码的输出目录
  serversPath: './src',

  // 生成的API文件输出目录
  apiPrefix: 'api/generated',

  // 项目名称
  projectName: 'cheese-ai-api',

  // 按标签分组生成文件
  organization: 'tags',

  // 忽略的接口
  ignore: [
    '/api/internal/**',
    '/api/test/**'
  ],
}
