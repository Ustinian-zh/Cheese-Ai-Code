declare namespace API {
  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseLoginUserVO = {
    code?: number
    data?: LoginUserVO
    message?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageUserVO = {
    code?: number
    data?: PageUserVO
    message?: string
  }

  type BaseResponseUser = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    message?: string
  }

  type BaseResponsePageChatHistory = {
    code?: number
    data?: PageChatHistory
    message?: string
  }

  type DeleteRequest = {
    id?: string | number
  }

  type getUserByIdParams = { id: string | number }

  type getUserVOByIdParams = { id: string | number }

  type LoginUserVO = {
    id?: string | number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
    updateTime?: string
  }

  type PageUserVO = {
    records?: UserVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type User = {
    id?: string | number
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    vipExpireTime?: string
    vipCode?: string
    vipNumber?: number
    shareCode?: string
    inviteUser?: string | number
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserAddRequest = {
    userName?: string
    userAccount?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserLoginRequest = {
    userAccount?: string
    userPassword?: string
  }

  type UserQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: string | number
    userName?: string
    userAccount?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterRequest = {
    userAccount?: string
    userPassword?: string
    checkPassword?: string
  }

  type UserUpdateRequest = {
    id?: string | number
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    id?: string | number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }

  /** 对话历史实体 */
  type ChatHistory = {
    id?: string | number
    message?: string
    messageType?: string
    appId?: string | number
    userId?: string | number
    parentId?: string | number
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  /** 对话历史查询请求（管理员分页，或服务侧构造） */
  type ChatHistoryQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: string | number
    message?: string
    messageType?: string
    appId?: string | number
    userId?: string | number
    lastCreateTime?: string
  }

  /** 对话历史分页 VO */
  type PageChatHistory = {
    records?: ChatHistory[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  // ============ App related types (frontend usage) ============
  type App = {
    id?: string | number
    userId?: string | number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    deployedTime?: string
    createTime?: string
    editTime?: string
  }

  type AppVO = {
    id?: string | number
    userId?: string | number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    user?: UserVO
    createTime?: string
    updateTime?: string
    deployedTime?: string
    editTime?: string
  }

  type AppQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: string | number
    userId?: string | number
    appName?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
  }

  type AppUpdateRequest = { id?: string | number; appName?: string }
  type AppAdminUpdateRequest = { id?: string | number; priority?: number; cover?: string; appName?: string }
  type AppDeployRequest = { appId?: string | number }

  type getAppVOByIdParams = { id: string | number }
  type getAppVOByIdByAdminParams = { id: string | number }

  type BaseResponseAppVO = { code?: number; data?: AppVO; message?: string }
  type PageAppVO = {
    records?: AppVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }
  type BaseResponsePageAppVO = { code?: number; data?: PageAppVO; message?: string }
  type BaseResponseString = { code?: number; data?: string; message?: string }
  type ServerSentEventString = string

  // chat APIs
  type chatToGenCodeParams = { appId: string | number; message: string }
  type AppAddRequest = { initPrompt?: string }
  type listAppChatHistoryParams = { appId: string; pageSize?: number; lastCreateTime?: string }
  type listAllChatHistoryByPageForAdminParams = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: string | number
    message?: string
    messageType?: string
    appId?: string | number
    userId?: string | number
  }
}
