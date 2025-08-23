package com.ustinian.cheeseaicode.core.saver;

import com.ustinian.cheeseaicode.ai.model.HtmlCodeResult;
import com.ustinian.cheeseaicode.ai.model.MultiFileCodeResult;
import com.ustinian.cheeseaicode.exception.BusinessException;
import com.ustinian.cheeseaicode.exception.ErrorCode;
import com.ustinian.cheeseaicode.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行相应的保存逻辑
 *
 * @author yupi
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaver = new HtmlCodeFileSaverTemplate();

    private static final MultiFileCodeFileSaverTemplate multiFileCodeFileSaver = new MultiFileCodeFileSaverTemplate();

    //region 执行代码保存appid版本
    /**
     * 执行代码保存（使用 appId）
     *
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @param appId       应用 ID
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE -> multiFileCodeFileSaver.saveCode((MultiFileCodeResult) codeResult, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        };
    }

    //endregion


//    /**
//     * 执行代码保存
//     *
//     * @param codeResult  代码结果对象
//     * @param codeGenType 代码生成类型
//     * @return 保存的目录
//     */
//    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType) {
//        return switch (codeGenType) {
//            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult);
//            case MULTI_FILE -> multiFileCodeFileSaver.saveCode((MultiFileCodeResult) codeResult);
//            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
//        };
//    }
}
