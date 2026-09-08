package com.xylanny.backend.exception;
import com.xylanny.backend.model.dto.Result;
import com.xylanny.backend.model.enums.BusinessCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// 提供日志能力，用于记录错误信息
@Slf4j
// 组合了@ControllerAdvice+@ResponseBody，表示这是一个全局增强类，异常处理结果会以JSON格式返回
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 指定该方法专门处理BusinessException类型的异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        // 记录日志，方便后端开发者排查问题
        log.error("业务异常", e);
        // 将异常中的code和message封装成统一的Result对象
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("系统异常", e);
        return Result.error(BusinessCode.SYSTEM_ERROR);
    }
}
