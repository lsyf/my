package com.loushuiyifan.config;

import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loushuiyifan.system.vo.JsonResult;

/**
 * @author 漏水亦凡
 * @create 2017-04-29 9:24.
 */
@ControllerAdvice
public class ExceptionAdvice {
    private Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);


    @ExceptionHandler({UnauthorizedException.class})
    @ResponseBody
    public JsonResult processError(Exception e) {
        logger.error("error", e);
        return JsonResult.failure("没有权限:"+e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public JsonResult processException(Exception e) {
        logger.error("error", e);
        return JsonResult.failure(e.getMessage());

    }


}
