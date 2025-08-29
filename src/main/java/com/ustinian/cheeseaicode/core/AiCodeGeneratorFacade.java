package com.ustinian.cheeseaicode.core;

import cn.hutool.json.JSONUtil;
import com.ustinian.cheeseaicode.ai.AiCodeGeneratorService;
import com.ustinian.cheeseaicode.ai.AiCodeGeneratorServiceFactory;
import com.ustinian.cheeseaicode.ai.model.HtmlCodeResult;
import com.ustinian.cheeseaicode.ai.model.MultiFileCodeResult;
import com.ustinian.cheeseaicode.core.parser.CodeParserExecutor;
import com.ustinian.cheeseaicode.core.saver.CodeFileSaverExecutor;
import com.ustinian.cheeseaicode.exception.BusinessException;
import com.ustinian.cheeseaicode.exception.ErrorCode;
import com.ustinian.cheeseaicode.model.enums.CodeGenTypeEnum;
import com.ustinian.cheeseaicode.model.message.AiResponseMessage;
import com.ustinian.cheeseaicode.model.message.ToolExecutedMessage;
import com.ustinian.cheeseaicode.model.message.ToolRequestMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;
    /**
     * 统一入口：根据类型生成并保存代码（使用 appId）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        // 根据 appId 获取对应的 AI 服务实例
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 统一入口：根据类型生成并保存代码（流式，使用 appId）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用 ID
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
 // 根据 appId 获取对应的 AI 服务实例
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            case VUE_PROJECT -> {
//                Flux<String> codeStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                TokenStream tokenStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                yield processTokenStream(tokenStream);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };

    }
    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息(反应式编程)适配器模式
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    .onCompleteResponse((ChatResponse response) -> {
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    .start();
        });
    }

    /**
     * 通用流式代码处理方法（使用 appId）
     *
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @param appId       应用 ID
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            // 流式返回完成后保存代码
            try {
                String completeCode = codeBuilder.toString();
                // 使用执行器解析代码
                Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
                // 使用执行器保存代码
                File savedDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenType, appId);
                log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败: {}", e.getMessage());
            }
        });
    }

//region 代码优化后的
//    /**
//     * 统一入口：根据类型生成并保存代码
//     *
//     * @param userMessage     用户提示词
//     * @param codeGenTypeEnum 生成类型
//     * @return 保存的目录
//     */
//    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
//        if (codeGenTypeEnum == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
//        }
//        return switch (codeGenTypeEnum) {
//            case HTML -> {
//                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
//                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML);
//            }
//            case MULTI_FILE -> {
//                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
//                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE);
//            }
//            default -> {
//                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
//            }
//        };
//    }
//    /**
//     * 统一入口：根据类型生成并保存代码（流式）
//     *
//     * @param userMessage     用户提示词
//     * @param codeGenTypeEnum 生成类型
//     */
//    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
//        if (codeGenTypeEnum == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
//        }
//        return switch (codeGenTypeEnum) {
//            case HTML -> {
//                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
//                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML);
//            }
//            case MULTI_FILE -> {
//                Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
//                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE);
//            }
//            default -> {
//                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
//            }
//        };
//    }
//    /**
//     * 通用流式代码处理方法
//     *
//     * @param codeStream  代码流
//     * @param codeGenType 代码生成类型
//     * @return 流式响应
//     */
//    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType) {
//        StringBuilder codeBuilder = new StringBuilder();
//        return codeStream.doOnNext(chunk -> {
//            // 实时收集代码片段
//            codeBuilder.append(chunk);
//        }).doOnComplete(() -> {
//            // 流式返回完成后保存代码
//            try {
//                String completeCode = codeBuilder.toString();
//                // 使用执行器解析代码
//                Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
//                // 使用执行器保存代码
//                File savedDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenType);
//                log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
//            } catch (Exception e) {
//                log.error("保存失败: {}", e.getMessage());
//            }
//        });
//    }
//endregion
//    /**
//     * 统一入口：根据类型生成并保存代码（流式）
//     *
//     * @param userMessage     用户提示词
//     * @param codeGenTypeEnum 生成类型
//     */
//    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
//        if (codeGenTypeEnum == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
//        }
//        return switch (codeGenTypeEnum) {
//            case HTML -> generateAndSaveHtmlCodeStream(userMessage);
//            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage);
//            default -> {
//                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
//            }
//        };
//    }
//
//    /**
//     * 统一入口：根据类型生成并保存代码
//     *
//     * @param userMessage     用户提示词
//     * @param codeGenTypeEnum 生成类型
//     * @return 保存的目录
//     */
//    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
//        if (codeGenTypeEnum == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
//        }
//        return switch (codeGenTypeEnum) {
//            case HTML -> generateAndSaveHtmlCode(userMessage);
//            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage);
//            default -> {
//                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
//            }
//        };
//    }
//
//    /**
//     * 生成 HTML 模式的代码并保存
//     *
//     * @param userMessage 用户提示词
//     * @return 保存的目录
//     */
//    private File generateAndSaveHtmlCode(String userMessage) {
//        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
//        return CodeFileSaver.saveHtmlCodeResult(result);
//    }
//
//    /**
//     * 生成多文件模式的代码并保存
//     *
//     * @param userMessage 用户提示词
//     * @return 保存的目录
//     */
//    private File generateAndSaveMultiFileCode(String userMessage) {
//        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
//        return CodeFileSaver.saveMultiFileCodeResult(result);
//    }
//    /**
//     * 生成 HTML 模式的代码并保存（流式）
//     *
//     * @param userMessage 用户提示词
//     * @return 保存的目录
//     */
//    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
//        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
//        // 当流式返回生成代码完成后，再保存代码
//        StringBuilder codeBuilder = new StringBuilder();
//        return result
//                .doOnNext(chunk -> {
//                    // 实时收集代码片段
//                    codeBuilder.append(chunk);
//                })
//                .doOnComplete(() -> {
//                    // 流式返回完成后保存代码
//                    try {
//                        String completeHtmlCode = codeBuilder.toString();
//                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
//                        // 保存代码到文件
//                        File savedDir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//                        log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
//                    } catch (Exception e) {
//                        log.error("保存失败: {}", e.getMessage());
//                    }
//                });
//    }
//
//    /**
//     * 生成多文件模式的代码并保存（流式）
//     *
//     * @param userMessage 用户提示词
//     * @return 保存的目录
//     */
//    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
//        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
//        // 当流式返回生成代码完成后，再保存代码
//        StringBuilder codeBuilder = new StringBuilder();
//        return result
//                .doOnNext(chunk -> {
//                    // 实时收集代码片段
//                    codeBuilder.append(chunk);
//                })
//                .doOnComplete(() -> {
//                    // 流式返回完成后保存代码
//                    try {
//                        String completeMultiFileCode = codeBuilder.toString();
//                        MultiFileCodeResult multiFileResult = CodeParser.parseMultiFileCode(completeMultiFileCode);
//                        // 保存代码到文件
//                        File savedDir = CodeFileSaver.saveMultiFileCodeResult(multiFileResult);
//                        log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
//                    } catch (Exception e) {
//                        log.error("保存失败: {}", e.getMessage());
//                    }
//                });
//    }

}
