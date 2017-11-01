package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="dim_cut_cfg",schema="charge")
public class RptImportDataCut {
	
	 String ruleId;
	 Integer latnId;
	 String incomeSource;
	 Integer shareType;
	 String express;
	 String activeFlag;
	 String chgWho;
	 String lstUpd;
	 String groupId;
}
