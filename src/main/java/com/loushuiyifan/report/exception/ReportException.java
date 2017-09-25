package com.loushuiyifan.report.exception;

/**
 * 存储异常
 * @author 漏水亦凡
 * @date 2017/9/20
 */
public class ReportException extends RuntimeException {

    public ReportException(String message) {
        super(message);
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }
}
