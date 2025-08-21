<script setup lang="ts">
import { reactive } from 'vue'
import { message } from 'ant-design-vue'
import { userLogin } from '@/api/userController'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const handleSubmit = async (values: API.UserLoginRequest) => {
  try {
    const res = await userLogin(values)
    if (res.data.code === 0) {
      message.success('登录成功')
      await loginUserStore.fetchLoginUser()
      router.push('/')
    } else {
      message.error('登录失败：' + res.data.message)
    }
  } catch (error: unknown) {
    message.error('登录失败：' + (error instanceof Error ? error.message : '未知错误'))
  }
}
</script>

<template>
  <div id="userLoginPage">
    <h2 class="title">奶酪 AI 代码生成 - 用户登录</h2>
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
      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>


<style scoped>
#userLoginPage {
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
#userLoginPage :deep(.ant-btn-primary) {
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

#userLoginPage :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #f57c00 0%, #ff9800 100%) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(255, 140, 66, 0.4) !important;
}

#userLoginPage :deep(.ant-btn-primary:active) {
  transform: translateY(0) !important;
  box-shadow: 0 2px 8px rgba(255, 140, 66, 0.3) !important;
}

/* 移除浏览器自动填充的黄色背景 */
#userLoginPage :deep(.ant-input:-webkit-autofill),
#userLoginPage :deep(.ant-input:-webkit-autofill:hover),
#userLoginPage :deep(.ant-input:-webkit-autofill:focus),
#userLoginPage :deep(.ant-input:-webkit-autofill:active) {
  -webkit-box-shadow: 0 0 0 30px #fff inset !important;
  box-shadow: 0 0 0 30px #fff inset !important;
  -webkit-text-fill-color: #000 !important;
  background-color: #fff !important;
  background-image: none !important;
}

#userLoginPage :deep(.ant-input-password .ant-input:-webkit-autofill),
#userLoginPage :deep(.ant-input-password .ant-input:-webkit-autofill:hover),
#userLoginPage :deep(.ant-input-password .ant-input:-webkit-autofill:focus),
#userLoginPage :deep(.ant-input-password .ant-input:-webkit-autofill:active) {
  -webkit-box-shadow: 0 0 0 30px #fff inset !important;
  box-shadow: 0 0 0 30px #fff inset !important;
  -webkit-text-fill-color: #000 !important;
  background-color: transparent !important;
  background-image: none !important;
}

</style>
