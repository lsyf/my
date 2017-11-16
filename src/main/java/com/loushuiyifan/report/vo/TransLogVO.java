package com.loushuiyifan.report.vo;

import lombok.Data;

@Data
public class TransLogVO {
    String month;         //账期
    String incomeSource;  //收入来源编码
    String incomeName;  //收入来源名称
    String codeName;    //本地网名称
    String batchId;     //批次号
    String subId;       //版本号
    String status;      //状态
    String createDate;  //创建时间
    String lstUpd;      //最后修改时间
    String voucherCode; //凭证号

    String userName;    //负责人
}
