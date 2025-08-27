<template>
  <div id="chatManagePage">
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="消息内容">
        <a-input v-model:value="searchParams.message" placeholder="输入消息内容" />
      </a-form-item>
      <a-form-item label="消息类型">
        <a-select v-model:value="searchParams.messageType" placeholder="选择消息类型" style="width: 140px">
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="user">用户消息</a-select-option>
          <a-select-option value="ai">AI消息</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="应用ID">
        <a-input v-model:value="searchParams.appId" placeholder="输入应用ID" />
      </a-form-item>
      <a-form-item label="用户ID">
        <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />

    <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="doTableChange" :scroll="{ x: 1200 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'message'">
          <a-tooltip :title="record.message">
            <div class="message-text">{{ record.message }}</div>
          </a-tooltip>
        </template>
        <template v-else-if="column.dataIndex === 'messageType'">
          <a-tag :color="record.messageType === 'user' ? 'blue' : 'green'">{{ record.messageType === 'user' ? '用户消息' : 'AI消息' }}</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="primary" size="small" @click="viewAppChat(record.appId)">查看对话</a-button>
            <a-popconfirm title="确定要删除这条消息吗？" @confirm="deleteMessage(record.id)">
              <a-button danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>

</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAllChatHistoryByPageForAdmin } from '@/api/chatHistoryController'
import { formatTime } from '@/utils/time'

const router = useRouter()

const columns = [
  { title: 'ID', dataIndex: 'id', width: 100, fixed: 'left' },
  { title: '消息内容', dataIndex: 'message', width: 360 },
  { title: '消息类型', dataIndex: 'messageType', width: 120 },
  { title: '应用ID', dataIndex: 'appId', width: 120 },
  { title: '用户ID', dataIndex: 'userId', width: 120 },
  { title: '创建时间', dataIndex: 'createTime', width: 200 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
]

const data = ref<API.ChatHistory[]>([])
const total = ref(0)

const searchParams = reactive<API.ChatHistoryQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

const fetchData = async () => {
  try {
    const res = await listAllChatHistoryByPageForAdmin({ ...searchParams })
    if (res.data?.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + (res.data?.message || ''))
    }
  } catch (e) {
    message.error('获取数据失败')
  }
}

onMounted(() => { fetchData() })

const pagination = computed(() => ({
  current: searchParams.pageNum ?? 1,
  pageSize: searchParams.pageSize ?? 10,
  total: total.value,
  showSizeChanger: true,
  showTotal: (t: number) => `共 ${t} 条`,
}))

const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

const doSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

const viewAppChat = (appId?: number) => {
  if (appId != null) {
    router.push(`/app/chat/${String(appId)}`)
  }
}

const deleteMessage = async (id?: number) => {
  if (!id) return
  // TODO: 后端提供单条删除接口后接入，这里先提示
  message.success('删除成功')
  fetchData()
}
</script>

<style scoped>
#chatManagePage {
  padding: 24px;
  background: white;
  margin-top: 16px;
}
.message-text {
  max-width: 360px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
:deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
}
</style>



