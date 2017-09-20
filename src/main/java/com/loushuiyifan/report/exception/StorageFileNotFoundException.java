package com.loushuiyifan.report.exception;

/**
 * @author 漏水亦凡
 * @date 2017/9/20
 */
public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
