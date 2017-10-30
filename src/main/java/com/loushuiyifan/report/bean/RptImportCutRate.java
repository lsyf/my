package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="charge.dim_cut_rate")
public class RptImportCutRate {
	private String ruleId;
	private Integer bureauId; // c5
	private Integer areaId; // c4
	private Double rate;
	private String acctMonth;
}
