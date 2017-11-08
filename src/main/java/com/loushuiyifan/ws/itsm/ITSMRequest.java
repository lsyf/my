package com.loushuiyifan.ws.itsm;

import lombok.Data;

import java.util.List;

/**
 * ITSM接口 访问请求
 *
 * @author 漏水亦凡
 * @date 2017/11/8
 */
@Data
public class ITSMRequest {

    String title;//标题
    String eipAccount;//EIP账号
    String month;//账期
    String latnId;//本地网
    List<String> importLogId;//汇总平台流水号
    String sumAmount;//本月累计导入金额
    String curAmount;//本次导入金额
    String remark;//导入说明
    List<Detail> detail;//明细表信息

    @Data
    public static class Detail {
        String c4Name;//县分
        String incomeName;//收入来源
        String amount;//导入金额
    }
}
