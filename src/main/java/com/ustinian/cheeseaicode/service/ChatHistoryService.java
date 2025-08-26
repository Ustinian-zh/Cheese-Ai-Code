package com.ustinian.cheeseaicode.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ustinian.cheeseaicode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.ustinian.cheeseaicode.model.entity.ChatHistory;
import com.ustinian.cheeseaicode.model.entity.User;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author <a href="https://github.com/Ustinian-zh">Ustinian-zh</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    /**
     * 新增对话历史
     * @param appId    id
     * @param message  消息
     * @param messageType 消息类型
     * @param userId   用户id
     * @return
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 关联删除
     * @param appId
     * @return
     */
    boolean deleteByAppId(Long appId);

    /**
     * 获取查询包装类
     * @param chatHistoryQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 游标查询核心服务
     * @param appId
     * @param pageSize
     * @param lastCreateTime
     * @param loginUser
     * @return
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);
}
