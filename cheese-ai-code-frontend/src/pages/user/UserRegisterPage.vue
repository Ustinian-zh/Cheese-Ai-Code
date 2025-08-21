<script setup lang="ts">
import { reactive } from 'vue'
import { message } from 'ant-design-vue'
import { userRegister } from '@/api/userController'
import { useRouter } from 'vue-router'

const router = useRouter()

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const validateCheckPassword = async (_rule: unknown, value: string) => {
  if (value !== formState.userPassword) {
    return Promise.reject('两次输入密码不一致')
  }
  return Promise.resolve()
}

const handleSubmit = async (values: API.UserRegisterRequest) => {
  try {
    const res = await userRegister(values)
    if (res.data.code === 0) {
      message.success('注册成功')
      router.push('/user/login')
    } else {
      message.error('注册失败：' + res.data.message)
    }
  } catch (error: unknown) {
    message.error('注册失败：' + (error instanceof Error ? error.message : '未知错误'))
  }
}
</script>

<template>
  <div id="userRegisterPage">
    <h2 class="title">奶酪 AI 代码生成 - 用户注册</h2>
    <div class="desc">不写一行代码，生成完整应用</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码不能小于 8 位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请确认密码' },
          { validator: validateCheckPassword, trigger: 'change' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请确认密码" />
      </a-form-item>
      <div class="tips">
        已有账号？
        <RouterLink to="/user/login">去登录</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
#userRegisterPage {
  max-width: 360px;
  margin: 0 auto;
  padding: 60px 0;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}

/* 按钮样式调整为橙色主题，参考右上角登录按钮 */
#userRegisterPage :deep(.ant-btn-primary) {
  height: 44px !important;
  border-radius: 22px !important;
  background: linear-gradient(135deg, #ff8c42 0%, #ffa726 100%) !important;
  border: none !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  box-shadow: 0 2px 8px rgba(255, 140, 66, 0.3) !important;
  transition: all 0.3s ease !important;
  font-family: 'SF Pro Display', 'PingFang SC', 'Hiragino Sans GB', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif !important;
}

#userRegisterPage :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #f57c00 0%, #ff9800 100%) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(255, 140, 66, 0.4) !important;
}

#userRegisterPage :deep(.ant-btn-primary:active) {
  transform: translateY(0) !important;
  box-shadow: 0 2px 8px rgba(255, 140, 66, 0.3) !important;
}

/* 移除浏览器自动填充的黄色背景 */
#userRegisterPage :deep(.ant-input:-webkit-autofill),
#userRegisterPage :deep(.ant-input:-webkit-autofill:hover),
#userRegisterPage :deep(.ant-input:-webkit-autofill:focus),
#userRegisterPage :deep(.ant-input:-webkit-autofill:active) {
  -webkit-box-shadow: 0 0 0 30px #fff inset !important;
  box-shadow: 0 0 0 30px #fff inset !important;
  -webkit-text-fill-color: #000 !important;
  background-color: #fff !important;
  background-image: none !important;
}

#userRegisterPage :deep(.ant-input-password .ant-input:-webkit-autofill),
#userRegisterPage :deep(.ant-input-password .ant-input:-webkit-autofill:hover),
#userRegisterPage :deep(.ant-input-password .ant-input:-webkit-autofill:focus),
#userRegisterPage :deep(.ant-input-password .ant-input:-webkit-autofill:active) {
  -webkit-box-shadow: 0 0 0 30px #fff inset !important;
  box-shadow: 0 0 0 30px #fff inset !important;
  -webkit-text-fill-color: #000 !important;
  background-color: transparent !important;
  background-image: none !important;
}

</style>
