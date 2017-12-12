package com.loushuiyifan.system;

/**
 * 存储异常
 * @author 漏水亦凡
 * @date 2017/9/20
 */
public class SystemException extends RuntimeException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
