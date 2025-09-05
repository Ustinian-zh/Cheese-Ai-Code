package com.ustinian.cheeseaicode.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class PromptSafetyInputGuardrail implements InputGuardrail {

    // 敏感词列表
    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "忽略之前的指令", "忽略之前的命令", "忽略上面的指令", "忽略上述指令",
            "ignore previous instructions", "ignore above", "ignore all previous",
            "forget previous instructions", "disregard previous instructions",
            "破解", "hack", "绕过", "bypass", "越狱", "jailbreak",
            "重新定义", "重新设定", "现在你是", "假装你是", "扮演", "角色扮演"
    );

    // 注入攻击模式
    private static final List<Pattern> INJECTION_PATTERNS = Arrays.asList(
            Pattern.compile("(?i)ignore\\s+(?:previous|above|all)\\s+(?:instructions?|commands?|prompts?)"),
            Pattern.compile("(?i)(?:forget|disregard)\\s+(?:everything|all)\\s+(?:above|before)"),
            Pattern.compile("(?i)(?:pretend|act|behave)\\s+(?:as|like)\\s+(?:if|you\\s+are)"),
            Pattern.compile("(?i)system\\s*:\\s*you\\s+are"),
            Pattern.compile("(?i)new\\s+(?:instructions?|commands?|prompts?)\\s*:")
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String input = userMessage.singleText();
        log.info("护轨检查输入: {}", input);
        
        // 检查输入长度
        if (input.length() > 1000) {
            log.warn("输入内容过长: {} 字符", input.length());
            return fatal("输入内容过长，不要超过 1000 字");
        }
        
        // 检查是否为空
        if (input.trim().isEmpty()) {
            log.warn("输入内容为空");
            return fatal("输入内容不能为空");
        }
        
        // 检查敏感词
        String lowerInput = input.toLowerCase();
        for (String sensitiveWord : SENSITIVE_WORDS) {
            if (lowerInput.contains(sensitiveWord.toLowerCase())) {
                log.warn("检测到敏感词: {}", sensitiveWord);
                return fatal("输入包含不当内容，请修改后重试");
            }
        }
        
        // 检查注入攻击模式
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                log.warn("检测到注入攻击模式: {}", pattern.pattern());
                return fatal("检测到恶意输入，请求被拒绝");
            }
        }
        
        log.info("护轨检查通过");
        return success();
    }
}
