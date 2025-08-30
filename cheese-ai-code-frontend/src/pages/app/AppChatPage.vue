<template>
  <div id="appChatPage">
    <div class="header-bar">
      <div class="header-left">
        <h1 class="app-name">{{ appInfo?.appName || '网站生成器' }}</h1>
        <a-tag v-if="appInfo?.codeGenType" color="blue" class="code-gen-type-tag">
          {{ formatCodeGenType(appInfo.codeGenType) }}
        </a-tag>
      </div>
      <div class="header-right">
        <a-button type="default" @click="showAppDetail">应用详情</a-button>
        <a-button
          type="primary"
          @click="downloadCode"
          :loading="downloading"
          :disabled="!isOwner"
        >
          <template #icon>
            <DownloadOutlined />
          </template>
          下载代码
        </a-button>
        <a-button type="primary" @click="deployApp" :loading="deploying">一键部署</a-button>
      </div>
    </div>

    <div class="main-content">
      <div class="chat-section" ref="chatSection">
        <div class="messages-container" ref="messagesContainer">
          <div v-if="hasMoreHistory" class="load-more-container">
            <a-button type="link" @click="loadMoreHistory" :loading="loadingHistory" size="small">加载更多历史消息</a-button>
          </div>
          <div v-for="(message, index) in messages" :key="index" class="message-item">
            <div v-if="message.type === 'user'" class="user-message">
              <div class="message-content">{{ message.content }}</div>
              <div class="message-avatar">
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              </div>
            </div>
            <div v-else class="ai-message">
              <div class="message-avatar">
                <a-avatar :src="aiAvatar" />
              </div>
              <div class="message-content">
                <MarkdownRenderer v-if="message.content" :content="message.content" />
                <div v-if="message.loading" class="loading-indicator">
                  <a-spin size="small" />
                  <span>AI 正在思考...</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="input-container">
          <div class="input-wrapper">
            <a-textarea
              v-model:value="userInput"
              placeholder="请描述你想生成的网站，越详细效果越好哦"
              :rows="4"
              :maxlength="1000"
              @keydown.enter.prevent="sendMessage"
              :disabled="isGenerating || !isOwner"
            />
            <div class="input-actions">
              <a-button type="primary" @click="sendMessage" :loading="isGenerating" :disabled="!isOwner">
                发送
              </a-button>
            </div>
          </div>
        </div>
      </div>

      <div class="preview-section">
        <div class="preview-header">
          <h3>生成后的网页展示</h3>
          <div class="preview-actions">
            <a-button v-if="previewUrl" type="link" @click="openInNewTab">新窗口打开</a-button>
          </div>
        </div>
        <div class="preview-content">
          <div v-if="!previewUrl && !isGenerating" class="preview-placeholder">
            <div class="placeholder-icon">🌐</div>
            <p>网站文件生成完成后将在这里展示</p>
          </div>
          <div v-else-if="isGenerating" class="preview-loading">
            <a-spin size="large" />
            <p>正在生成网站...</p>
          </div>
          <iframe v-else :src="previewUrl" class="preview-iframe" frameborder="0" @load="onIframeLoad"></iframe>
        </div>
      </div>
    </div>

    <AppDetailModal
      v-model:open="appDetailVisible"
      :app="appInfo"
      :show-actions="isOwner || isAdmin"
      @edit="editApp"
      @delete="deleteApp"
    />

    <DeploySuccessModal v-model:open="deployModalVisible" :deploy-url="deployUrl" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { DownloadOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { getAppVoById, deployApp as deployAppApi, deleteApp as deleteAppApi } from '@/api/appController'
import { listAppChatHistory } from '@/api/chatHistoryController'
import { CodeGenTypeEnum, formatCodeGenType } from '@/utils/codeGenTypes'
import request from '@/request'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import AppDetailModal from '@/components/AppDetailModal.vue'
import DeploySuccessModal from '@/components/DeploySuccessModal.vue'
import aiAvatar from '@/assets/aiAvatar.png'
import { API_BASE_URL, getStaticPreviewUrl } from '@/config/env'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const appInfo = ref<API.AppVO>()
const appId = ref<string>()

interface Message {
  type: 'user' | 'ai'
  content: string
  loading?: boolean
}
const messages = ref<Message[]>([])
const userInput = ref('')
const isGenerating = ref(false)
const messagesContainer = ref<HTMLElement | null>(null)
const chatSection = ref<HTMLElement | null>(null)
// 对话历史游标与加载控制
const loadingHistory = ref(false)
const hasMoreHistory = ref(false)
const lastCreateTime = ref<string>()
const historyLoaded = ref(false)

const previewUrl = ref('')
const previewReady = ref(false)

const deploying = ref(false)
const deployModalVisible = ref(false)
const deployUrl = ref('')
// 下载相关
const downloading = ref(false)

const isOwner = computed(() => String(appInfo.value?.userId ?? '') === String(loginUserStore.loginUser.id ?? ''))
const isAdmin = computed(() => loginUserStore.loginUser.userRole === 'admin')
const appDetailVisible = ref(false)

const showAppDetail = () => { appDetailVisible.value = true }

const fetchAppInfo = async () => {
  const id = route.params.id as string
  if (!id) {
    message.error('应用ID不存在')
    router.push('/')
    return
  }
  appId.value = id
  try {
    const res = await getAppVoById({ id } as any)
    if (res.data.code === 0 && res.data.data) {
      appInfo.value = res.data.data
      // 先加载对话历史
      await loadChatHistory()
      // 如有至少 2 条对话历史，则展示网站
      if (messages.value.length >= 2) {
        updatePreview()
      }
      // 若是自己的应用且无历史，自动发送 initPrompt
      if (appInfo.value.initPrompt && isOwner.value && messages.value.length === 0 && historyLoaded.value) {
        await sendInitialMessage(appInfo.value.initPrompt)
      }
    } else {
      message.error('获取应用信息失败')
      router.push('/')
    }
  } catch (e) {
    message.error('获取应用信息失败')
    router.push('/')
  }
}

const sendInitialMessage = async (prompt: string) => {
  messages.value.push({ type: 'user', content: prompt } as Message)
  const aiMessageIndex = messages.value.length
  messages.value.push({ type: 'ai', content: '', loading: true } as Message)
  await nextTick(); scrollToBottom()
  isGenerating.value = true
  await generateCode(prompt, aiMessageIndex)
}

const sendMessage = async () => {
  if (!userInput.value.trim() || isGenerating.value) return
  const messageText = userInput.value.trim()
  userInput.value = ''
  messages.value.push({ type: 'user', content: messageText } as Message)
  const aiMessageIndex = messages.value.length
  messages.value.push({ type: 'ai', content: '', loading: true } as Message)
  await nextTick(); scrollToBottom()
  isGenerating.value = true
  await generateCode(messageText, aiMessageIndex)
}

const generateCode = async (userMessage: string, aiMessageIndex: number) => {
  let eventSource: EventSource | null = null
  let streamCompleted = false
  try {
    const baseURL = request.defaults.baseURL || API_BASE_URL
    const params = new URLSearchParams({ appId: appId.value || '', message: userMessage })
    const url = `${baseURL}/app/chat/gen/code?${params}`
    eventSource = new EventSource(url, { withCredentials: true })
    let fullContent = ''
    eventSource.onmessage = function (event) {
      if (streamCompleted) return
      try {
        const parsed = JSON.parse(event.data)
        const content = parsed.d
        if (content !== undefined && content !== null) {
          fullContent += content
          messages.value[aiMessageIndex].content = fullContent
          messages.value[aiMessageIndex].loading = false
          scrollToBottom()
        }
      } catch (error) {
        handleError(error, aiMessageIndex)
      }
    }
    eventSource.addEventListener('done', function () {
      if (streamCompleted) return
      streamCompleted = true
      isGenerating.value = false
      eventSource?.close()
      setTimeout(async () => { await fetchAppInfo(); updatePreview() }, 800)
    })
    eventSource.onerror = function () {
      if (streamCompleted || !isGenerating.value) return
      if (eventSource?.readyState === EventSource.CONNECTING) {
        streamCompleted = true
        isGenerating.value = false
        // 重要：在连接失败（浏览器保持 CONNECTING）时，显式结束卡片的“正在思考”状态，避免无限转圈
        if (messages.value[aiMessageIndex]) {
          messages.value[aiMessageIndex].loading = false
        }
        eventSource?.close()
        setTimeout(async () => { await fetchAppInfo(); updatePreview() }, 800)
      } else {
        handleError(new Error('SSE连接错误'), aiMessageIndex)
      }
    }
  } catch (error) {
    handleError(error, aiMessageIndex)
  }
}

// 加载对话历史（游标分页）
const loadChatHistory = async (isLoadMore = false) => {
  if (!appId.value || loadingHistory.value) return
  loadingHistory.value = true
  try {
    const params: { appId: string; pageSize: number; lastCreateTime?: string } = {
      appId: appId.value,
      pageSize: 10,
    }
    if (isLoadMore && lastCreateTime.value) {
      params.lastCreateTime = lastCreateTime.value
    }
    const res = await listAppChatHistory(params)
    const pageData = res.data?.data
    const chatHistories = pageData?.records ?? []
    if (chatHistories.length > 0) {
      const historyMessages: Message[] = chatHistories
        .map((chat: any) => ({
          type: (chat.messageType === 'user' ? 'user' : 'ai') as 'user' | 'ai',
          content: chat.message || '',
        }))
        .reverse()
      if (isLoadMore) {
        messages.value.unshift(...historyMessages)
      } else {
        messages.value = historyMessages
      }
      lastCreateTime.value = chatHistories[chatHistories.length - 1]?.createTime
      hasMoreHistory.value = chatHistories.length === 10
    } else {
      hasMoreHistory.value = false
    }
    historyLoaded.value = true
  } catch (e) {
    message.error('加载对话历史失败')
  } finally {
    loadingHistory.value = false
  }
}

const loadMoreHistory = async () => {
  await loadChatHistory(true)
}

const handleError = (error: unknown, aiMessageIndex: number) => {
  messages.value[aiMessageIndex].content = '抱歉，生成过程中出现了错误，请重试。'
  messages.value[aiMessageIndex].loading = false
  message.error('生成失败，请重试')
  isGenerating.value = false
}

const updatePreview = () => {
  if (appId.value) {
    const codeGenType = appInfo.value?.codeGenType || CodeGenTypeEnum.HTML
    const newPreviewUrl = getStaticPreviewUrl(codeGenType, appId.value)
    previewUrl.value = newPreviewUrl
    previewReady.value = true
  }
}

const scrollToBottom = () => {
  const scroller = chatSection.value || messagesContainer.value
  if (scroller) {
    scroller.scrollTop = scroller.scrollHeight
  }
}

const deployApp = async () => {
  if (!appId.value) { message.error('应用ID不存在'); return }
  deploying.value = true
  try {
    const res = await deployAppApi({ appId: appId.value as any })
    if (res.data.code === 0 && res.data.data) {
      deployUrl.value = res.data.data
      deployModalVisible.value = true
      message.success('部署成功')
    } else {
      message.error('部署失败：' + res.data.message)
    }
  } catch (e) {
    message.error('部署失败，请重试')
  } finally {
    deploying.value = false
  }
}

const openInNewTab = () => { if (previewUrl.value) window.open(previewUrl.value, '_blank') }
const onIframeLoad = () => { previewReady.value = true }

const editApp = () => { if (appInfo.value?.id) router.push(`/app/edit/${appInfo.value.id}`) }
const deleteApp = async () => {
  if (!appInfo.value?.id) return
  try {
    const res = await deleteAppApi({ id: appInfo.value.id as any })
    if (res.data.code === 0) { message.success('删除成功'); appDetailVisible.value = false; router.push('/') }
    else { message.error('删除失败：' + res.data.message) }
  } catch (e) { message.error('删除失败') }
}

// 下载代码
const downloadCode = async () => {
  if (!appId.value) {
    message.error('应用ID不存在')
    return
  }
  downloading.value = true
  try {
    const baseURL = request.defaults.baseURL || API_BASE_URL || ''
    // 规范化 appId，避免意外的花括号/符号导致 404
    const idStr = String(appId.value).trim()
    const normalizedId = (idStr.match(/\d+/)?.[0]) || ''
    if (!normalizedId) {
      message.error('应用ID无效')
      return
    }
    const url = `${baseURL}/app/download/${normalizedId}`
    const response = await fetch(url, {
      method: 'GET',
      credentials: 'include',
      headers: { Accept: 'application/zip,application/json;q=0.9,*/*;q=0.8' },
    })
    if (!response.ok) {
      if (response.status === 401) {
        message.warning('请先登录')
        const redirect = encodeURIComponent(window.location.href)
        router.push(`/user/login?redirect=${redirect}`)
        return
      }
      const contentType = response.headers.get('content-type') || ''
      let errorMsg = `下载失败: ${response.status}`
      try {
        if (contentType.includes('application/json')) {
          const data = await response.json()
          errorMsg = (data && (data.message || data.description)) || errorMsg
        } else {
          const text = await response.text()
          if (text) errorMsg = text
        }
      } catch {}
      throw new Error(errorMsg)
    }
    const contentDisposition = response.headers.get('content-disposition') || response.headers.get('Content-Disposition')
    const fileNameMatch = contentDisposition?.match(/filename="(.+)"/)
    const fileName = fileNameMatch?.[1] || `app-${appId.value}.zip`
    const blob = await response.blob()
    const downloadUrl = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(downloadUrl)
    message.success('代码下载成功')
  } catch (error) {
    console.error('下载失败：', error)
    message.error('下载失败，请重试')
  } finally {
    downloading.value = false
  }
}

onMounted(() => { fetchAppInfo() })
onUnmounted(() => {})
</script>

<script lang="ts">
export default {
  methods: {},
}
</script>

<style scoped>
#appChatPage { height: 100%; display: flex; flex-direction: column; padding: 16px; background: #fdfdfd; }
.header-bar { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; }
.header-left { display: flex; align-items: center; gap: 12px; flex: 1; min-width: 0; }
.app-name { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a1a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.header-right { display: flex; gap: 12px; }
.main-content { flex: 1; display: flex; gap: 16px; padding: 8px; overflow: hidden; }
.chat-section { flex: 2; display: flex; flex-direction: column; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); height: 700px; overflow: auto; }
.messages-container { flex: 1; padding: 16px; overflow-y: auto; scroll-behavior: smooth; }
.load-more-container { text-align: center; padding: 8px 0; margin-bottom: 8px; }
.message-item { margin-bottom: 12px; }
.user-message { display: flex; justify-content: flex-end; align-items: flex-start; gap: 8px; }
.ai-message { display: flex; justify-content: flex-start; align-items: flex-start; gap: 8px; }
.message-content { max-width: 83%; padding: 12px 16px; border-radius: 12px; line-height: 1.5; word-wrap: break-word; }
.user-message .message-content { background: #1890ff; color: white; }
.ai-message .message-content { background: #f5f5f5; color: #1a1a1a; padding: 8px 12px; }
.message-avatar { flex-shrink: 0; }
.loading-indicator { display: flex; align-items: center; gap: 8px; color: #666; }
.input-container { padding: 16px; background: white; }
.input-wrapper { position: relative; }
.input-wrapper .ant-input { padding-right: 50px; }
.input-actions { position: absolute; bottom: 8px; right: 8px; }
.preview-section { flex: 3; display: flex; flex-direction: column; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden; }
.preview-header { display: flex; justify-content: space-between; align-items: center; padding: 16px; border-bottom: 1px solid #e8e8e8; }
.preview-header h3 { margin: 0; font-size: 16px; font-weight: 600; }
.preview-actions { display: flex; gap: 8px; }
.preview-content { flex: 1; position: relative; overflow: hidden; }
.preview-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #666; }
.placeholder-icon { font-size: 48px; margin-bottom: 16px; }
.preview-loading { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #666; }
.preview-loading p { margin-top: 16px; }
.preview-iframe { width: 100%; height: 100%; border: none; }
.code-gen-type-tag { font-size: 12px; }
@media (max-width: 1024px) { .main-content { flex-direction: column; } .chat-section, .preview-section { flex: none; height: 50vh; } }
@media (max-width: 768px) { .header-bar { padding: 12px 16px; } .app-name { font-size: 16px; } .main-content { padding: 8px; gap: 8px; } .message-content { max-width: 85%; } }
</style>



