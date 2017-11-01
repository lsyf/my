package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="Rpt_import_datatax")
public class RptImportDataTax {
	 Long logId;
	 String acctMonth;
	 String prctr;// 2016税务 区域
	 Double aftTaxValue;// 2016税务 税后
}
