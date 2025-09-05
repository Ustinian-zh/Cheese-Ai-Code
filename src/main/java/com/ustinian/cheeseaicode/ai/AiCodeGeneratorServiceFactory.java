package com.ustinian.cheeseaicode.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ustinian.cheeseaicode.ai.tools.ToolManager;
import com.ustinian.cheeseaicode.exception.BusinessException;
import com.ustinian.cheeseaicode.exception.ErrorCode;
import com.ustinian.cheeseaicode.guardrail.PromptSafetyInputGuardrail;
import com.ustinian.cheeseaicode.model.enums.CodeGenTypeEnum;
import com.ustinian.cheeseaicode.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.util.List;

@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    // 使用多例Bean避免依赖自动配置
    // @Resource(name = "chatModel")
    // private ChatModel chatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private ToolManager toolManager;
    // OpenAI 兼容的流式模型
    // 说明：容器里名为 "openAiStreamingChatModel" 的 Bean 实际类型是 OpenAiStreamingChatModel。
    // 如果字段类型写成 StreamingChatModel，会出现 BeanNotOfRequiredTypeException（类型不兼容）。
    // 因此这里使用“具体类型”并配合 @Qualifier 精确注入该 Bean。
    // 使用多例Bean避免依赖自动配置
    // @Resource
    // @Qualifier("openAiStreamingChatModel")
    // private OpenAiStreamingChatModel openAiStreamingChatModel;

    // 推理流式模型（我们自定义配置产生）。
    // 保持接口类型，通过 @Qualifier 指定注入的 Bean 名称，避免与自动装配的其它同类 Bean 冲突。
    // 使用多例Bean避免依赖自动配置
    // @Resource
    // @Qualifier("reasoningStreamingChatModel")
    // private StreamingChatModel reasoningStreamingChatModel;

    /**
     * 默认提供一个 Bean
     */
    @Bean
    @Lazy
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L);
    }
    /**
     * AI 服务实例缓存  Long 改成了String
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，缓存键: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）这个方法是为了兼容历史逻辑
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }

    /**
     * 根据 appId 和代码生成类型获取服务（带缓存）
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(long appId, CodeGenTypeEnum codeGenType) {
        return appId + "_" + codeGenType.getValue();
    }
    /**
     * 创建新的 AI 服务实例
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(100)
                .build();
        // 从数据库加载历史对话到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
        // 根据代码生成类型选择不同的模型配置
        return switch (codeGenType) {
            case VUE_PROJECT -> {
                // 使用多例模式的 StreamingChatModel 解决并发问题
                StreamingChatModel reasoningStreamingChatModel = applicationContext.getBean("reasoningStreamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorService.class)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemoryProvider(memoryId -> chatMemory)
                        .tools(toolManager.getAllTools())
                        .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                                toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name()
                        ))
                        .inputGuardrails(List.of(new PromptSafetyInputGuardrail()))  // 添加输入护轨
//                        .outputGuardrails(new RetryOutputGuardrail())流式输出不支持输出护轨
                        .build();
            }
            //    @InputGuardrails({ FirstInputGuardrail.class, SecondInputGuardrail.class })给方法加护轨
            case HTML, MULTI_FILE -> {
                // 使用多例模式的 ChatModel 和 StreamingChatModel 解决并发问题
                ChatModel chatModel = applicationContext.getBean("routingChatModelPrototype", ChatModel.class);
                StreamingChatModel openAiStreamingChatModel = applicationContext.getBean("streamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(openAiStreamingChatModel)
                        .chatMemory(chatMemory)
                        .inputGuardrails(new PromptSafetyInputGuardrail())  // 添加输入护轨
//                        .outputGuardrails(new RetryOutputGuardrail())为了流式输出
                        .build();
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "不支持的代码生成类型: " + codeGenType.getValue());
        };

    }



}

