package com.ustinian.cheeseaicode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ustinian.cheeseaicode.constant.AppConstant;
import com.ustinian.cheeseaicode.core.AiCodeGeneratorFacade;
import com.ustinian.cheeseaicode.core.handler.StreamHandlerExecutor;
import com.ustinian.cheeseaicode.exception.BusinessException;
import com.ustinian.cheeseaicode.exception.ErrorCode;
import com.ustinian.cheeseaicode.exception.ThrowUtils;
import com.ustinian.cheeseaicode.mapper.AppMapper;
import com.ustinian.cheeseaicode.model.dto.app.AppQueryRequest;
import com.ustinian.cheeseaicode.model.entity.App;
import com.ustinian.cheeseaicode.model.entity.User;
import com.ustinian.cheeseaicode.model.enums.ChatHistoryMessageTypeEnum;
import com.ustinian.cheeseaicode.model.enums.CodeGenTypeEnum;
import com.ustinian.cheeseaicode.model.vo.AppVO;
import com.ustinian.cheeseaicode.model.vo.UserVO;
import com.ustinian.cheeseaicode.service.AppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/Ustinian-zh">Ustinian-zh</a>
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{
    @Resource
    private UserServiceImpl userService;
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Resource
    private ChatHistoryServiceImpl chatHistoryService;
    @Resource
    private StreamHandlerExecutor streamHandlerExecutor ;

    /**
     * 实现获取应用详情加密后，逻辑为先详细后封装
     * @param app
     * @return
     */
    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    /**
     * 构造查询单个对象的方法
     * @param appQueryRequest 请求参数
     * @return 查询对象
     */
    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    /**
     *  批量获取应用详情
     * @param appList 应用列表
     * @return 应用详情列表
     */
    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                //应该是set自动去重的
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                //如果遇到相同的id，合并一下用一个，否则会异常
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    /**
     * ChatToGenCode方法，调用门面生成代码，并返回结果
     * @param appId
     * @param message
     * @param loginUser
     * @return
     */
    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
//        // 4. 获取应用的代码生成类型
//        String codeGenTypeStr = app.getCodeGenType();
//        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
//        if (codeGenTypeEnum == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
//        }
//        // 5. 调用 AI 生成代码
//        return aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
// 4. 决定本次生成应使用的代码生成类型（支持从用户消息中识别“单 HTML / 多文件”意图）
        CodeGenTypeEnum currentEnum = CodeGenTypeEnum.getEnumByValue(app.getCodeGenType());
        if (currentEnum == null) {
            currentEnum = CodeGenTypeEnum.MULTI_FILE; // 安全兜底
        }
        CodeGenTypeEnum decidedEnum = decideCodeGenTypeFromMessage(message, currentEnum);

// 若识别的类型与数据库不一致，回写数据库，确保后续展示 / 部署一致
        if (!decidedEnum.getValue().equals(app.getCodeGenType())) {
            App update = new App();
            update.setId(appId);
            update.setCodeGenType(decidedEnum.getValue());
            update.setEditTime(LocalDateTime.now());
            boolean updated = this.updateById(update);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新应用生成类型失败");
        }

// 5. 通过校验后，添加用户消息到对话历史
        chatHistoryService.addChatMessage(appId, message, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());
// 6. 调用 AI 生成代码（流式）
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, decidedEnum, appId);
// 7. 收集 AI 响应内容并在完成后记录到对话历史
        return streamHandlerExecutor.doExecute(codeStream, chatHistoryService, appId, loginUser,  decidedEnum);

    }

    /**
     * 网站应用部署
     * @param appId
     * @param loginUser
     * @return
     */
    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限部署该应用，仅本人可以部署
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
        }
        // 4. 检查是否已有 deployKey
        String deployKey = app.getDeployKey();
        // 没有则生成 6 位 deployKey（大小写字母 + 数字）
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 5. 获取代码生成类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        //路径分隔符File.separator
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
        // 7. 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            //文件复制工具类
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }
        // 8. 更新应用的 deployKey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 9. 返回可访问的 URL
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }

    /**
     * 根据用户输入的自然语言判定生成类型。
     * - 命中“单 html / 单页 / 单文件”等关键词 → HTML
     * - 命中“多文件 / 多页 / 组件 / 目录结构”等关键词 → MULTI_FILE
     * - 否则沿用当前类型
     */
    private CodeGenTypeEnum decideCodeGenTypeFromMessage(String userMessage, CodeGenTypeEnum current) {
        if (StrUtil.isBlank(userMessage)) {
            return current;
        }
        String msg = userMessage.toLowerCase();
        String alnum = msg.replaceAll("[^a-z0-9\\u4e00-\\u9fa5]", "");
        boolean wantHtml = alnum.contains("html") || alnum.contains("单html") || alnum.contains("单页") || alnum.contains("单文件");
        boolean wantMulti = alnum.contains("多文件") || alnum.contains("多页") || alnum.contains("组件化") || alnum.contains("目录结构");
        // Vue 工程关键词：出现这些词且未明显指向 HTML/多文件时，判定为 VUE_PROJECT
        boolean wantVue = alnum.contains("vue") || alnum.contains("vite") || alnum.contains("工程")
                || alnum.contains("项目") || alnum.contains("路由") || alnum.contains("pinia")
                || alnum.contains("antdesignvue") || alnum.contains("antdv") || alnum.contains("组件库");
        if (wantHtml && !wantMulti) {
            return CodeGenTypeEnum.HTML;
        }
        if (wantMulti && !wantHtml) {
            return CodeGenTypeEnum.MULTI_FILE;
        }
        if (wantVue && !wantHtml && !wantMulti) {
            return CodeGenTypeEnum.VUE_PROJECT;
        }
        return current;
    }
    /**
     * 删除应用时关联删除对话历史
     *重写removebyid
     * @param id 应用ID
     * @return 是否成功
     */
    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        // 转换为 Long 类型
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        // 先删除关联的对话历史
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            // 记录日志但不阻止应用删除
            log.error("删除应用关联对话历史失败: {}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }

}
