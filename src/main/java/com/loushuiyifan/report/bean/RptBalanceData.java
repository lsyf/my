package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="RPT_BALANCE_DATA" , schema = "charge")
public class RptBalanceData {
	String reportMonth	;			
	String indexCode	;     //指标编码
	String indexName	;	  //指标名称
	String txtMessage	;	  //文本信息
	String prctr	    ;	  //利润中心编码
	String prctrName	;			
	String kunnr	    ;		//客户编码
	String kunnrName	;		//客户名称
	String lifnr	    ;		//供应商编码
	String lifnrName	;	    //供应商名称
	String sapFinCode	;		//对应SAP科目编码
	String sapFinCodeName;	    //对应SAP科目名称
	String shbk1	;		    //特殊总账标识
	String koart	;			//SAP科目属性
	String hkont1	;			
	String regionId	;			
	String reportId	;			
	String reportName	;			
	Double balance	;		

}
