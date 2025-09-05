<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
import { normalizeCodeGenType } from '@/utils/codeGenTypes'
import { getDeployUrl } from '@/config/env'
import AppCard from '@/components/AppCard.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const userPrompt = ref('')
const creating = ref(false)

const myApps = ref<API.AppVO[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

const featuredApps = ref<API.AppVO[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

const setPrompt = (prompt: string) => {
  userPrompt.value = prompt
}

const createApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }
  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }
  creating.value = true
  try {
    const res = await addApp({ initPrompt: userPrompt.value.trim() })
    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (e) {
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) return
  const res = await listMyAppVoByPage({
    pageNum: myAppsPage.current,
    pageSize: myAppsPage.pageSize,
    sortField: 'createTime',
    sortOrder: 'desc',
  })
  if (res.data.code === 0 && res.data.data) {
    myApps.value = res.data.data.records || []
    myAppsPage.total = res.data.data.totalRow || 0
    // 规范化展示（防止后端返回大小写/下划线不一致导致展示错乱）
    myApps.value.forEach(app => { (app as any).codeGenType = normalizeCodeGenType(app.codeGenType as any) })
  }
}

const loadFeaturedApps = async () => {
  const res = await listGoodAppVoByPage({
    pageNum: featuredAppsPage.current,
    pageSize: featuredAppsPage.pageSize,
    sortField: 'createTime',
    sortOrder: 'desc',
  })
  if (res.data.code === 0 && res.data.data) {
    featuredApps.value = res.data.data.records || []
    featuredAppsPage.total = res.data.data.totalRow || 0
    featuredApps.value.forEach(app => { (app as any).codeGenType = normalizeCodeGenType(app.codeGenType as any) })
  }
}

const viewChat = (appId: string | number | undefined) => {
  if (appId) router.push(`/app/chat/${String(appId)}?view=1`)
}

const viewWork = (app: API.AppVO) => {
  const key = (app as any)?.deployKey
  if (key && typeof key === 'string' && key.trim()) {
    const url = getDeployUrl(key)
    window.open(url, '_blank')
  }
}

onMounted(() => {
  loadMyApps()
  loadFeaturedApps()
})
</script>

<template>
  <div id="homePage">
    <div class="container">
      <div class="hero-section">
        <h1 class="hero-title">奶酪应用工坊</h1>
        <p class="hero-description">一句话轻松创建网站应用</p>
      </div>

      <div class="input-section">
        <a-textarea
          v-model:value="userPrompt"
          placeholder="帮我创建个人博客网站"
          :rows="4"
          :maxlength="1000"
          class="prompt-input"
        />
        <div class="input-actions">
          <a-button type="primary" size="large" @click="createApp" :loading="creating">
            <template #icon>
              <span>↑</span>
            </template>
          </a-button>
        </div>
      </div>

      <div class="quick-actions">
        <a-button type="default" @click="setPrompt('创建一个现代化的个人博客网站，包含文章列表、文章详情、分类标签、搜索功能、评论系统和个人简介页面。采用响应式设计，支持深色/浅色主题切换，具有优雅的排版和流畅的动画效果。')">个人博客网站</a-button>
        <a-button type="default" @click="setPrompt('设计一个专业的企业官网，包含首页轮播、公司介绍、产品展示、新闻资讯、联系我们等页面。采用商务风格设计，突出企业形象和品牌价值，支持多语言切换，具备SEO优化和移动端适配。')">企业官网</a-button>
        <a-button type="default" @click="setPrompt('构建一个功能完整的在线商城，包含商品展示、购物车、用户登录注册、订单管理、支付流程等功能。采用现代电商设计风格，支持商品筛选、搜索、收藏，具有良好的用户体验和购物流程。')">在线商城</a-button>
        <a-button type="default" @click="setPrompt('制作一个精美的作品展示网站，包含作品集展示、项目详情、技能介绍、个人经历和联系方式。采用创意设计风格，突出视觉效果，支持图片懒加载、瀑布流布局和作品分类筛选功能。')">作品展示网站</a-button>
      </div>

      <div class="section">
        <h2 class="section-title">我的作品</h2>
        <div class="app-grid">
          <AppCard v-for="app in myApps" :key="app.id" :app="app" @view-chat="viewChat" @view-work="viewWork" />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsPage.current"
            v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps"
          />
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">精选案例</h2>
        <div class="featured-grid">
          <AppCard v-for="app in featuredApps" :key="app.id" :app="app" :featured="true" @view-chat="viewChat" @view-work="viewWork" />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
}
.container { max-width: 1200px; margin: 0 auto; padding: 20px; }
.hero-section { text-align: center; padding: 60px 0 40px; margin-bottom: 20px; color: #1e293b; }
.hero-title { font-size: 40px; font-weight: 700; line-height: 1.2; letter-spacing: .5px; margin: 0 0 12px; font-family: "HarmonyOS Sans SC","MiSans","SF Pro Display","PingFang SC","Microsoft YaHei","Helvetica Neue",Helvetica,Arial,sans-serif; background: linear-gradient(135deg,#ffb36a 0%, #ff8c42 60%, #e56a00 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; text-shadow: 0 2px 8px rgba(229, 106, 0, 0.18), 0 0 12px rgba(255, 140, 66, 0.15); -webkit-text-stroke: 0.5px rgba(255, 255, 255, 0.25); }
.hero-description { font-size: 16px; color: #64748b; margin: 0; }
.input-section { position: relative; margin: 0 auto 24px; max-width: 800px; }
.prompt-input { border-radius: 12px; font-size: 16px; padding: 16px 56px 16px 16px; }
.input-actions { position: absolute; bottom: 12px; right: 12px; }
.quick-actions { display: flex; gap: 12px; justify-content: center; margin-bottom: 40px; flex-wrap: wrap; }
.section { margin-bottom: 40px; }
.section-title { font-size: 22px; font-weight: 600; margin-bottom: 16px; color: #1e293b; }
.app-grid, .featured-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 24px; margin-bottom: 16px; }
.pagination-wrapper { display: flex; justify-content: center; margin-top: 16px; }
@media (max-width: 768px) { .hero-title { font-size: 28px; } .hero-description { font-size: 14px; } .app-grid, .featured-grid { grid-template-columns: 1fr; } }
</style>



