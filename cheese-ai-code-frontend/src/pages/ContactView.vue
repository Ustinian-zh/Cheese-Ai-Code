<template>
  <div class="contact-page">
    <div class="hero-section">
      <h1 class="page-title">联系我们</h1>
      <p class="page-subtitle">有任何问题或建议？我们很乐意听取您的意见</p>
    </div>

    <div class="contact-grid">
      <div class="contact-info">
        <h2 class="section-title">联系方式</h2>
        <div class="contact-items">
          <div class="contact-item">
            <div class="contact-icon">📧</div>
            <div class="contact-details">
              <h3>邮箱地址</h3>
              <p>contact@cheese-ai.com</p>
            </div>
          </div>
          <div class="contact-item">
            <div class="contact-icon">🏢</div>
            <div class="contact-details">
              <h3>办公地址</h3>
              <p>北京市海淀区中关村软件园</p>
            </div>
          </div>
          <div class="contact-item">
            <div class="contact-icon">📱</div>
            <div class="contact-details">
              <h3>微信群</h3>
              <p>扫码加入奶酪AI开发者社区</p>
            </div>
          </div>
          <div class="contact-item">
            <div class="contact-icon">💬</div>
            <div class="contact-details">
              <h3>QQ群</h3>
              <p>888888888</p>
            </div>
          </div>
        </div>
      </div>

      <div class="contact-form">
        <h2 class="section-title">发送消息</h2>
        <a-form :model="formData" layout="vertical">
          <a-form-item label="姓名" name="name">
            <a-input v-model:value="formData.name" placeholder="请输入您的姓名" />
          </a-form-item>
          <a-form-item label="邮箱" name="email">
            <a-input v-model:value="formData.email" placeholder="请输入您的邮箱" />
          </a-form-item>
          <a-form-item label="主题" name="subject">
            <a-input v-model:value="formData.subject" placeholder="请输入消息主题" />
          </a-form-item>
          <a-form-item label="消息内容" name="message">
            <a-textarea
              v-model:value="formData.message"
              placeholder="请输入您的消息内容"
              :rows="6"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" class="submit-btn" @click="handleSubmit">
              发送消息
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>

    <div class="demo-notice">
      <div class="notice-card">
        <div class="notice-icon">💡</div>
        <div class="notice-content">
          <h3>配置化路由演示</h3>
          <p>
            这个页面通过配置化路由自动生成，无需手动添加路由和菜单项。
            只需在 <code>src/config/routes.ts</code> 中添加配置即可！
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { message } from 'ant-design-vue'

interface FormData {
  name: string
  email: string
  subject: string
  message: string
}

const formData = reactive<FormData>({
  name: '',
  email: '',
  subject: '',
  message: ''
})

const handleSubmit = () => {
  // 这里可以添加表单验证和提交逻辑
  console.log('表单数据:', formData)
  message.success('消息发送成功！我们会尽快回复您。')

  // 重置表单
  Object.keys(formData).forEach(key => {
    (formData as any)[key] = ''
  })
}
</script>

<style scoped>
.contact-page {
  padding: 40px 0;
}

.hero-section {
  text-align: center;
  margin-bottom: 60px;
}

.page-title {
  font-size: 3rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 50%, #ffb74d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.page-subtitle {
  font-size: 1.2rem;
  color: #64748b;
  margin-bottom: 0;
}

.contact-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 60px;
}

.contact-info,
.contact-form {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 32px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 140, 66, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 24px;
  text-align: center;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.contact-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 140, 66, 0.05);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.contact-item:hover {
  background: rgba(255, 140, 66, 0.1);
  transform: translateX(4px);
}

.contact-icon {
  font-size: 1.5rem;
  width: 40px;
  text-align: center;
}

.contact-details h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.contact-details p {
  color: #64748b;
  margin: 0;
  font-size: 0.9rem;
}

.submit-btn {
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 100%);
  border: none;
  border-radius: 8px;
  font-weight: 600;
  padding: 0 32px;
  height: 40px;
  width: 100%;
  box-shadow: 0 2px 8px rgba(255, 140, 66, 0.3);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #f57c00 0%, #ff9800 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 140, 66, 0.4);
}

.demo-notice {
  display: flex;
  justify-content: center;
}

.notice-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(255, 140, 66, 0.1);
  border: 1px solid rgba(255, 140, 66, 0.2);
  border-radius: 12px;
  padding: 20px 24px;
  max-width: 600px;
}

.notice-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notice-content h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.notice-content p {
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

.notice-content code {
  background: rgba(255, 140, 66, 0.2);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.85rem;
  color: #ff8c42;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .contact-page {
    padding: 24px 0;
  }

  .page-title {
    font-size: 2.5rem;
  }

  .contact-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .contact-info,
  .contact-form {
    padding: 24px 16px;
  }

  .notice-card {
    margin: 0 16px;
    flex-direction: column;
    text-align: center;
  }
}
</style>
