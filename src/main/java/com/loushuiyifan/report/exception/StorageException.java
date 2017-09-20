package com.loushuiyifan.report.exception;

/**
 * @author 漏水亦凡
 * @date 2017/9/20
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
