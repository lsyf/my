package com.loushuiyifan.web.error;

import com.loushuiyifan.web.bean.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author 漏水亦凡
 * @create 2017-04-29 9:24.
 */
@ControllerAdvice
public class ExceptionAdvice {
    private Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);


//    @ExceptionHandler({Exception.class})
//    public ModelAndView processError(Exception e) {
//        ModelAndView mv = new ModelAndView();
//        mv.addObject("error", e);
//        mv.setViewName("error");
//        logger.error("error", e);
//        return mv;
//    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> processException(Exception e) {
        logger.error("error", e);
        return new ResponseEntity<>(JsonResult.failure(e.getMessage()), new HttpHeaders(), HttpStatus.OK);

    }


}
