package com.loushuiyifan.report.bean;

import java.util.Date;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name ="rpt_income_source_sts")
public class RptIncomeSourceSts {
	String acctMonth;
	String incomeSource;
	String codeName;
	String status;
	Date   updateDate;
	Long   rowNum;
	String operator;
}
