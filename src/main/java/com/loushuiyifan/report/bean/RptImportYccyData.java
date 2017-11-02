package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="rpt_import_data_ssc")
public class RptImportYccyData {
	 Long logId;
	 Long latnId;
	 String horCode;
	 String verCode;
	 Double indexData;
	 String acctMonth;
	 String reportNo;
}
