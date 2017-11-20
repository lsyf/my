package com.loushuiyifan.report.vo;

import lombok.Data;

@Data
public class FundsAuditVO {
				
	String txtMessage   ;   //文本信息
	String prctr  ;    //利润中心编码
	String sapFinCode ; //SAP科目编码
	String   sapFinCodeName;  //SAP科目名称
	String   kunnr;          //客户编码
	String   kunnrName;    //客户名称
	String jieFbalance ; //借方金额
	String daiFbalance ; //贷方金额
}
