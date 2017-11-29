package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author 漏水亦凡
 * @date 2017/11/24
 */
@Table(name = "rpt_excel_wyf")
@Data
public class RptExcelWyf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select seq_rpt_excel_wyf.nextval from dual")
    Long excelId;
    String fileName;
    String incomeSource;
    String areaName;
    String acctMonth;
    String filePath;
    String incomesourceId;
    String praentIncomId;
    String createTime;

}
