package com.loushuiyifan.report.vo;

import lombok.Data;

@Data
public class FundsFeeVO {
	
	String indexCode ; //指标编码
	String indexName;   //指标名称
	String balance ;   //金额
	String prctr  ;    //利润中心编码
	String prctrName ;  //利润中心简称
	String sapFinCode ; //SAP科目编码
	
}
