package com.ustinian.cheeseaicode.debug;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class BeanChecker implements ApplicationRunner {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public void run(ApplicationArguments args) {
        log.info("=== 检查所有ChatModel类型的Bean ===");
        String[] chatModelBeans = applicationContext.getBeanNamesForType(ChatModel.class);
        log.info("ChatModel Bean names: {}", Arrays.toString(chatModelBeans));
        
        log.info("=== 检查所有StreamingChatModel类型的Bean ===");
        String[] streamingChatModelBeans = applicationContext.getBeanNamesForType(StreamingChatModel.class);
        log.info("StreamingChatModel Bean names: {}", Arrays.toString(streamingChatModelBeans));
        
        log.info("=== 检查所有Bean名称（包含chat关键字）===");
        String[] allBeans = applicationContext.getBeanDefinitionNames();
        Arrays.stream(allBeans)
            .filter(name -> name.toLowerCase().contains("chat"))
            .forEach(name -> log.info("Chat related bean: {}", name));
    }
}