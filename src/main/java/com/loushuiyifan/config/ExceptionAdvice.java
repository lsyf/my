package com.loushuiyifan.config;

import com.loushuiyifan.system.vo.JsonResult;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 漏水亦凡
 * @create 2017-04-29 9:24.
 */
@ControllerAdvice
public class ExceptionAdvice {
    private Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);


    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView processError(Exception e) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("title", "无权限访问");
        mv.addObject("error", e);
        mv.setViewName("error");
        logger.error("error", e);
        return mv;
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public JsonResult processException(Exception e) {
        logger.error("error", e);
        return JsonResult.failure(e.getMessage());

    }


}
