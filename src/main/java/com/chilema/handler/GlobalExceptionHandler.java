package com.chilema.handler;

import com.chilema.common.MyException;
import com.chilema.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
/**
 * 全局异常处理类
 */
public class GlobalExceptionHandler {
    /**
     * sql异常处理
     *
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> ExceptionHandler(SQLIntegrityConstraintViolationException exception) {
        String msg = exception.getMessage();
        log.error(msg);
        if (msg.contains("Duplicate entry")) {
            return Result.error(msg.split(" ")[2] + "已存在");
        }
        return Result.error("发生未知错误");
    }

    /**
     * 自定义异常处理
     *
     * @param myException
     * @return
     */
    @ExceptionHandler(MyException.class)
    public Result<String> MyExceptionHandler(MyException myException) {
        log.error(myException.getMessage());
        return Result.error(myException.getMessage());
    }
}
