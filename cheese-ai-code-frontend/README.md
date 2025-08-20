# 🧀 奶酪AI代码平台 - 前端

## 快速开始

```bash
# 安装依赖
npm install

# 开发运行
npm run dev

# 生产构建
npm run build
```

## 技术栈

- Vue 3 + TypeScript
- Vite + Ant Design Vue
- Vue Router + Pinia

## 项目结构

```
src/
├── components/    # 组件
├── pages/         # 页面
├── layouts/       # 布局
├── router/        # 路由
├── utils/         # 工具
├── types/         # 类型
└── constants/     # 常量
```

## 开发说明

- 后端开发阶段，前端保持简洁
- 使用配置化路由管理
- 支持OpenAPI自动生成API代码
- 内置中英文国际化支持

## API开发

```bash
# 生成API代码（连接后端后使用）
npm run api:generate
```