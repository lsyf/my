package com.loushuiyifan.report.vo;

import lombok.Data;

@Data
public class SettleDataVO {
	String logId;
	String reportId;
	String reportName;
	String month;
	String incomeSource;
	String status;
	String fileSeq;
	String createDate;
	String importDate;
	
	String fileName;
	String serviceId;
}
