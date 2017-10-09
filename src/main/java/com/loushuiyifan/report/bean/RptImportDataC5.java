package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
@Data
@Table(name = "rpt_import_data_c5")
public class RptImportDataC5 {
    Long logId;

    Integer areaId;

    String horCode;

    String verCode;

    Double indexData;

    String acctMonth;

    String incomeSource;

    Long c5Id;

    Integer latnid;
    String incomeCode;

    Double taxValue;

    Integer selfCode;

    String contractId;
    String contractName;

    String sSecondClaimId;
    String sSecondClaimName;
    String gisGrid;
    String gisGrname;


}
