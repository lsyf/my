package com.loushuiyifan.report.vo;

import lombok.Data;

/**
 * 通用 导入记录
 *
 * @author 漏水亦凡
 * @date 2017/9/26
 */
@Data
public class ImportDataLogVO {

    String month;
    Long logId;
    String fileName;//文件名
    Integer num;    //数量
    Double sum;     //合计金额
    Double sum2;     //收入金额
    Long userId;    //用户ID
    String userName;    //用户名(nickname)
    String city;    //地市
    String action;  //导入状态
    String remark;  //导入说明

    String isItsm;
    String itsmStatus;
    String itsmOrderNo;
    String itsmUrl;
    String eip;

}
