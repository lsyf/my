package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="charge.dim_cut_cfg")
public class RptImportDataCut {
	
	private String ruleId;
	private Integer latnId;
	private String incomeSource;
	private Integer shareType;
	private String express;
	private String activeFlag;
	private String chgWho;
	private String lstUpd;
	private String groupId;
}
