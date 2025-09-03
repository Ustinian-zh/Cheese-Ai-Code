package com.ustinian.cheeseaicode.langgraph4j.ai;

import com.ustinian.cheeseaicode.langgraph4j.tools.ImageSearchTool;
import com.ustinian.cheeseaicode.langgraph4j.tools.LogoGeneratorTool;
import com.ustinian.cheeseaicode.langgraph4j.tools.MermaidDiagramTool;
import com.ustinian.cheeseaicode.langgraph4j.tools.UndrawIllustrationTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 图片收集服务工厂
 */
@Slf4j
@Configuration
public class ImageCollectionServiceFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Resource
    private ImageSearchTool imageSearchTool;

    @Resource
    private UndrawIllustrationTool undrawIllustrationTool;

    @Resource
    private MermaidDiagramTool mermaidDiagramTool;

    @Resource
    private LogoGeneratorTool logoGeneratorTool;

    /**
     * 创建图片收集 AI 服务
     */
    @Bean
    @Lazy
    public ImageCollectionService createImageCollectionService() {
        // 使用多例Bean避免依赖自动配置
        ChatModel routingChatModel = applicationContext.getBean("routingChatModelPrototype", ChatModel.class);
        return AiServices.builder(ImageCollectionService.class)
                .chatModel(routingChatModel)
                .tools(
                        imageSearchTool,
                        undrawIllustrationTool,
                        mermaidDiagramTool,
                        logoGeneratorTool
                )
                .build();
    }
}
