package com.loushuiyifan.report.bean;

import java.util.Date;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="LOG_rpt_import_data_ssc")
public class ExtImportYccyLog {
	Long logId;
	Integer latnId;
	String incomeSource;	
	String fileName;	
	String acctMonth;
	
	Integer userId;
	
	Date importDate;
	
	String status;
	
	String exportDesc;
	
	
	
	
	
}
