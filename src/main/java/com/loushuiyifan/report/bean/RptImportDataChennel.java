package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
@Data
@Table(name = "rpt_import_data_channel")
public class RptImportDataChennel {
    Long logId;

    Integer areaId;		     //c4Id

    String horCode;		     //客户群ID 

    String verCode;		     //指标编码

    Double indexData;   	//金额

    String acctMonth;   	//账期

    String incomeSource;  	//收入来源编码

    Long c5Id;

    Double taxValue;		//税金
    Double aftTaxValue;
    Integer selfCode;   	//自服务编码

    String action;
    String sourceName;
    String contractId;  	//承包单元编码
    String contractName;
    String zzxiulf;     	//关联交易类型编码
    String zglks;			//关联客商编码
    String ictCode;			//合同号
    String hkont;			//供应商
    String itemCode;		//ict项目名


}
