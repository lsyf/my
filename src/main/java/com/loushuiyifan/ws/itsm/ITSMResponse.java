package com.loushuiyifan.ws.itsm;

import lombok.Data;

/**
 * ITSM接口返回值
 *
 * @author 漏水亦凡
 * @date 2017/11/8
 */
@Data
public class ITSMResponse {

    String statusCode;
    String message;
    String itsmOrderId;
    String itsmUrl;
}
