package com.ustinian.cheeseaicode.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.ustinian.cheeseaicode.annotation.AuthCheck;
import com.ustinian.cheeseaicode.common.BaseResponse;
import com.ustinian.cheeseaicode.common.ResultUtils;
import com.ustinian.cheeseaicode.constant.UserConstant;
import com.ustinian.cheeseaicode.exception.ErrorCode;
import com.ustinian.cheeseaicode.exception.ThrowUtils;
import com.ustinian.cheeseaicode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.ustinian.cheeseaicode.model.entity.ChatHistory;
import com.ustinian.cheeseaicode.model.entity.User;
import com.ustinian.cheeseaicode.service.ChatHistoryService;
import com.ustinian.cheeseaicode.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 对话历史 控制层。
 *
 * @author <a href="https://github.com/Ustinian-zh">Ustinian-zh</a>
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {
    @Resource
    private UserService userService;
    @Resource
    private ChatHistoryService chatHistoryService;
    /**
     * 分页查询某个应用的对话历史（游标查询）
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条记录的创建时间
     * @param request        请求
     * @return 对话历史分页
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(@PathVariable Long appId,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(required = false) LocalDateTime lastCreateTime,
                                                              HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Page<ChatHistory> result = chatHistoryService.listAppChatHistoryByPage(appId, pageSize, lastCreateTime, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询所有对话历史
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 对话历史分页
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistory>> listAllChatHistoryByPageForAdmin(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = chatHistoryQueryRequest.getPageNum();
        long pageSize = chatHistoryQueryRequest.getPageSize();
        // 查询数据
        QueryWrapper queryWrapper = chatHistoryService.getQueryWrapper(chatHistoryQueryRequest);
        Page<ChatHistory> result = chatHistoryService.page(Page.of(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(result);
    }


//region 自动生成的代码
//    @Autowired
//    private ChatHistoryService chatHistoryService;
//
//    /**
//     * 保存对话历史。
//     *
//     * @param chatHistory 对话历史
//     * @return {@code true} 保存成功，{@code false} 保存失败
//     */
//    @PostMapping("save")
//    public boolean save(@RequestBody ChatHistory chatHistory) {
//        return chatHistoryService.save(chatHistory);
//    }
//
//    /**
//     * 根据主键删除对话历史。
//     *
//     * @param id 主键
//     * @return {@code true} 删除成功，{@code false} 删除失败
//     */
//    @DeleteMapping("remove/{id}")
//    public boolean remove(@PathVariable Long id) {
//        return chatHistoryService.removeById(id);
//    }
//
//    /**
//     * 根据主键更新对话历史。
//     *
//     * @param chatHistory 对话历史
//     * @return {@code true} 更新成功，{@code false} 更新失败
//     */
//    @PutMapping("update")
//    public boolean update(@RequestBody ChatHistory chatHistory) {
//        return chatHistoryService.updateById(chatHistory);
//    }
//
//    /**
//     * 查询所有对话历史。
//     *
//     * @return 所有数据
//     */
//    @GetMapping("list")
//    public List<ChatHistory> list() {
//        return chatHistoryService.list();
//    }
//
//    /**
//     * 根据主键获取对话历史。
//     *
//     * @param id 对话历史主键
//     * @return 对话历史详情
//     */
//    @GetMapping("getInfo/{id}")
//    public ChatHistory getInfo(@PathVariable Long id) {
//        return chatHistoryService.getById(id);
//    }
//
//    /**
//     * 分页查询对话历史。
//     *
//     * @param page 分页对象
//     * @return 分页对象
//     */
//    @GetMapping("page")
//    public Page<ChatHistory> page(Page<ChatHistory> page) {
//        return chatHistoryService.page(page);
//    }
//endregion
}
