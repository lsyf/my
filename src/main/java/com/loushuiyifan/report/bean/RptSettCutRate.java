package com.loushuiyifan.report.bean;

import java.util.Date;

import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name="RPT_SETT_CUT_RATE")
public class RptSettCutRate {
	private Long logId;
	private String reportId;
	private String reportName;
	private String acctMonth;
	private Integer latnId;
	private String areaName;
	private String zbCode;
	private String zbName;
	private Double rate;
	private Long userId;
	private Date lstUpd;
}
