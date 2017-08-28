package com.loushuiyifan.web.bean;

import java.io.Serializable;

/**
 * @author 漏水亦凡
 * @create 2017-04-26 11:17.
 */
public class JsonResult implements Serializable{
    private static final String OK = "ok";
    private static final String ERROR = "error";

    private Boolean state;
    private String msg;
    private Object data;

    private JsonResult(boolean state, String msg, Object data) {
        this.state = state;
        this.msg = msg;
        this.data = data;
    }

    public static JsonResult success() {
        return success(OK, null);
    }

    public static JsonResult success(String msg) {
        return success(msg, null);
    }

    public static JsonResult success(Object data) {
        return success(OK, data);
    }

    public static JsonResult success(String msg, Object data) {
        JsonResult resp = new JsonResult(true, msg, data);
        return resp;
    }

    public static JsonResult failure() {
        return failure(ERROR, null);
    }

    public static JsonResult failure(String msg) {
        return failure(msg, null);
    }

    public static JsonResult failure(Object data) {
        return failure(ERROR, data);
    }

    public static JsonResult failure(String msg, Object data) {
        JsonResult resp = new JsonResult(false, msg, data);
        return resp;
    }


    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
