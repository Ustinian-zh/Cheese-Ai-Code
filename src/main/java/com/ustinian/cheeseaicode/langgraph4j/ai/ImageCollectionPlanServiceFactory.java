package com.ustinian.cheeseaicode.langgraph4j.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class ImageCollectionPlanServiceFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 使用多例Bean避免依赖自动配置
    // @Resource(name = "chatModel")
    // private ChatModel chatModel;

    /**
     * 创建图片收集计划服务实例（工厂方法，每次调用创建新实例）
     */
    public ImageCollectionPlanService createImageCollectionPlanService() {
        // 每次获取新的多例Bean实例
        ChatModel chatModel = applicationContext.getBean("routingChatModelPrototype", ChatModel.class);
        return AiServices.builder(ImageCollectionPlanService.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 默认提供一个Bean（用于依赖注入）
     */
    @Bean
    @Lazy
    public ImageCollectionPlanService imageCollectionPlanService() {
        return createImageCollectionPlanService();
    }
}
