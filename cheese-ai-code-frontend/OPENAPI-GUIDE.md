# 🚀 OpenAPI 自动代码生成指南

## 📖 概述

本项目集成了 `@umijs/openapi` 工具，可以根据后端的 Swagger 接口文档自动生成前端请求代码，包括 TypeScript 类型定义。这种方式比手写接口代码更高效、更可靠。

## ✨ 功能特点

- **🎯 类型安全**：自动生成 TypeScript 类型定义
- **📝 注释完整**：根据 Swagger 文档生成详细注释
- **🔄 自动同步**：后端接口变更时快速同步
- **⚡ 高效开发**：无需手写接口代码
- **🏷️ 按标签分组**：自动按 Swagger 标签分组生成文件

## 🛠️ 已完成配置

### 1. 依赖安装
```bash
npm i --save-dev @umijs/openapi tslib rimraf
```

### 2. 配置文件 (`openapi2ts.config.ts`)
```typescript
export default {
  // 请求库路径，指向我们的request配置
  requestLibPath: "import request from '@/utils/request'",
  
  // Swagger接口文档地址
  schemaPath: 'http://localhost:8123/api/v3/api-docs',
  
  // 生成代码的输出目录
  serversPath: './src',
}
```

### 3. NPM 脚本命令
```json
{
  "scripts": {
    "openapi2ts": "openapi2ts",
    "api:generate": "npm run openapi2ts",
    "api:clean": "rimraf src/api/generated",
    "api:refresh": "npm run api:clean && npm run api:generate"
  }
}
```

### 4. Request 配置适配
在 `src/utils/request.ts` 中添加了 OpenAPI 兼容的默认导出函数。

## 🚀 使用方法

### 1. 启动后端服务
确保后端服务已启动并提供 Swagger 文档访问地址：
```
http://localhost:8123/api/v3/api-docs
```

### 2. 修改配置
根据实际情况修改 `openapi2ts.config.ts` 中的 `schemaPath`：
```typescript
// 开发环境
schemaPath: 'http://localhost:8123/api/v3/api-docs'

// 或使用本地JSON文件
schemaPath: './swagger-docs.json'
```

### 3. 生成代码
```bash
# 生成 API 代码
npm run api:generate

# 清理并重新生成
npm run api:refresh
```

### 4. 使用生成的代码
```typescript
// 导入自动生成的API函数
import { healthCheck } from '@/api/jiankangjiancha'
import { userLogin, getCurrentUser } from '@/api/yonghuguanli'
import { generateCode, analyzeCode } from '@/api/aIfuwu'

// 使用示例
const result = await healthCheck()
const loginResult = await userLogin({
  username: 'test',
  password: 'password'
})
```

## 📁 生成的文件结构

```
src/api/
├── typings.d.ts       # TypeScript 类型定义
├── index.ts           # 统一导出
├── jiankangjiancha.ts # 健康检查相关 API
├── yonghuguanli.ts    # 用户管理相关 API
└── aIfuwu.ts         # AI服务相关 API
```

## 🎯 API 函数示例

### 生成的函数格式
```typescript
/** 健康检查 检查服务健康状态 GET /health */
export async function healthCheck(options?: { [key: string]: any }) {
  return request<API.BaseResponse>('/health', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 用户登录 用户登录接口 POST /user/login */
export async function userLogin(body: API.LoginRequest, options?: { [key: string]: any }) {
  return request<API.LoginResponse>('/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
```

### 生成的类型定义
```typescript
declare namespace API {
  type LoginRequest = {
    /** 用户名 */
    username: string
    /** 密码 */
    password: string
    /** 验证码 */
    captcha?: string
  }

  type LoginResponse = BaseResponse & {
    data?: { user?: User; token?: string }
  }
}
```

## 🔧 高级配置

### 1. 自定义生成选项
```typescript
export default {
  requestLibPath: "import request from '@/utils/request'",
  schemaPath: 'http://localhost:8123/api/v3/api-docs',
  serversPath: './src',
  
  // 生成配置
  projectName: 'cheese-ai-api',
  apiPrefix: '@/api/generated',
  mock: false,
  
  // 组织方式
  organization: 'tags', // 按tag分组
  
  // 自定义函数名
  hook: {
    customFunctionName: (data) => {
      return data.operationId || `${data.method}${data.path.replace(/[^a-zA-Z0-9]/g, '')}`
    }
  },
  
  // 忽略的接口
  ignore: [
    '/api/internal/**'
  ]
}
```

### 2. 环境相关配置
```typescript
// 根据环境切换API文档地址
const getSchemaPath = () => {
  if (process.env.NODE_ENV === 'development') {
    return 'http://localhost:8123/api/v3/api-docs'
  }
  return 'https://api.cheese-ai.com/v3/api-docs'
}

export default {
  schemaPath: getSchemaPath(),
  // ...其他配置
}
```

## 📋 开发流程

### 1. 后端开发完成接口
- 后端开发者完成接口开发
- 更新 Swagger 文档注释
- 启动后端服务

### 2. 前端同步接口
```bash
# 重新生成接口代码
npm run api:refresh
```

### 3. 使用新接口
```typescript
// 直接使用生成的函数
import { newApiFunction } from '@/api/newController'

const result = await newApiFunction(params)
```

## 🎮 测试页面

访问 `/openapi-test` 页面可以：
- 测试生成的 API 函数
- 查看函数调用示例
- 了解代码生成特点
- 学习最佳实践

## 💡 最佳实践

1. **定期同步**：后端接口更新后及时重新生成
2. **类型检查**：利用 TypeScript 类型检查避免错误
3. **文档维护**：后端保持 Swagger 文档的准确性
4. **版本管理**：生成的文件不需要提交到 Git
5. **错误处理**：生成的函数会自动使用全局错误处理

## 🔗 相关链接

- [OpenAPI 官方文档](https://github.com/umijs/openapi)
- [Swagger 规范](https://swagger.io/specification/)
- [项目 API 演示](/api-demo)
- [OpenAPI 测试页面](/openapi-test)
