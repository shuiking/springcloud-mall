package com.cloud.mall.cloudproduct.exception;

import com.cloud.common.exception.BizCodeEnum;
import com.cloud.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理异常
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.cloud.mall.cloudproduct.controller")
public class MallExceptionAdvice {
    @ExceptionHandler(value = MethodArgumentNotValidException.class )
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验问题{},异常类型{}",e.getMessage(),e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String,String> map=new HashMap<>();
        result.getFieldErrors().forEach((item)->{
            String message = item.getDefaultMessage();
            String field = item.getField();
            map.put(field,message);
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMessage() ).put("data",map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable e){
        return R.error(BizCodeEnum.UNKNOW_EXEPTION.getCode(), BizCodeEnum.UNKNOW_EXEPTION.getMessage());
    }

}
