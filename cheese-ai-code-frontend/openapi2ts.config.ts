export default {
  // 请求库路径，指向项目现有的 request 客户端
  requestLibPath: "import request from '@/request'",

  // Swagger接口文档地址
  // 开发环境使用本地mock数据，生产环境使用实际API文档
  schemaPath: process.env.NODE_ENV === 'development'
    ? './mock/swagger.json'
    : 'http://localhost:8123/api/v3/api-docs',

  // 生成代码的输出目录
  serversPath: './src',

  // 生成的API文件输出目录（生成器内部使用的前缀，保留不变即可）
  apiPrefix: '""',

  // 项目名称（同时作为输出目录名）。将其固定为 'api'，生成到 src/api 下
  projectName: 'api',

  // 按标签分组生成文件
  organization: 'tags',

  // 忽略的接口
  ignore: [
    '/api/internal/**',
    '/api/test/**'
  ],
}
