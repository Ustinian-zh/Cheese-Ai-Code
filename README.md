# 🧀 奶酪AI代码平台

​		我开发了一个基于 Spring Boot 和 Vue 3 的 奶酪AI代码平台。下面是本项目的功能概述。

- 整合了多个 AI 模型，同时实现了**AI智能路由**，用户可以通过自然语言描述需求，系统会**自动**选择最合适的 AI 模型生成代码。
- 项目采用**流式响应**技术，用户可以实时看到代码生成过程。
- 提供**可视化编辑**功能，用户可以进入编辑模式，在右侧代码预览框内选中元素，调用工具可视化修改内容。
- 提供生成的项目**源码下载**功能。
- 支持一键部署功能，并通过网页截图技术结合COS存储**自动生成应用封面图**。
- 管理员可以进行**精选应用**，同时可以管理用户，应用和对话历史。系统会缓存精选应用。
- 利用Prompt 安全审查的护轨机制简单的**防止恶意提示词注入**和攻击。
- 支持**多用户并发**生成应用。
- 基于令牌桶算法使用Redisson分布式**限流**，进行流量保护。
- 输出护轨，代码生成过程采用**自动重试**策略。

![image-20250909105118119](https://cdn.jsdelivr.net/gh/Ustinian-zh/Cloud-Image-Hosting/Typora/20250909105118259.png)

### 🧾项目目录结构

```
Cheese-Ai-Code/
├── src/main/java/com/ustinian/cheeseaicode/
│   ├── ai/                     # AI相关功能
│   │   ├── tools/             # AI工具调用
│   │   └── model/             # AI模型管理
│   ├── core/                  # 核心业务逻辑
│   │   ├── handler/           # 流式响应处理
│   │   ├── parser/            # 代码解析
│   │   └── saver/             # 文件保存
│   ├── controller/            # REST API控制器
│   ├── service/               # 业务服务层
│   ├── config/                # 配置类
│   └── ratelimiter/           # 限流组件
├── cheese-ai-code-frontend/   # Vue3前端项目
│   ├── src/
│   │   ├── components/        # 组件
│   │   ├── pages/             # 页面
│   │   ├── stores/            # 状态管理
│   │   └── utils/             # 工具函数
│   └── public/                # 静态资源
└── docs/                      # 项目文档
```

### 🐱‍💻技术选型

后端技术栈

- **Spring Boot 3.5.4** - 现代化Java企业级开发框架
- **Java 21** - 最新LTS版本
- **MyBatis-Flex** - 灵活的ORM框架
- **LangChain4j** - Java版本的LLM应用开发框架
- **Redis + Redisson** - 缓存和分布式锁
- **MySQL** - 数据持久化
- **腾讯云COS** - 对象存储服务

前端技术栈

- **Vue 3** + **TypeScript** - 现代化前端框架
- **Ant Design Vue** - 企业级UI组件库
- **Vite** - 快速构建工具
- **Pinia** - 状态管理

### 📎项目体验步骤

详细步骤见：https://github.com/Ustinian-zh/Cheese-Ai-Code/releases/tag/v1.0.0

## 📚开发过程及细节

​	你可以访问[我的技术博客](https://www.cnblogs.com/Mistiest)来获取本项目开发的其他细节🍻
