// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 按应用游标查询对话历史（后端返回按 createTime 降序） */
export async function listAppChatHistory(
  params: {
    appId: string | number
    pageSize?: number
    lastCreateTime?: string
  },
  options?: { [key: string]: any }
) {
  const { appId, pageSize, lastCreateTime } = params
  return request<API.BaseResponsePageChatHistory>(`/chatHistory/app/${appId}` as any, {
    method: 'GET',
    params: {
      pageSize,
      lastCreateTime,
    },
    ...(options || {}),
  })
}

/** 管理员分页查询所有对话历史 */
export async function listAllChatHistoryByPageForAdmin(
  body: API.ChatHistoryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageChatHistory>('/chatHistory/admin/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}



