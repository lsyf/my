package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="stat_to_group")
public class StatToGroup {
	private String batchId;
	private String subId;
	private String acctMonth;
	private Long latnId;
	private String rptType;
	private Long rptCaseId;
	private String createDate;
	private String status;
	private String lstUpd;
	private String incomeSource;
}
