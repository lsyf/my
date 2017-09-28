package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
@Data
@Table(name = "rpt_import_data_ict")
public class RptImportDataICT {
    Long logId;

    Integer areaId;

    String horCode;

    String verCode;

    Double indexData;

    String acctMonth;

    String incomeSource;

    Long c5Id;

    Double taxValue;
    Double aftTaxValue;
    Integer selfCode;

    String action;
    String sourceName;
    String contractId;
    String contractName;
    String zzxiulf;
    String zglks;
    String ictCode;
    String hkont;
    String itemCode;

    String remark;


}
