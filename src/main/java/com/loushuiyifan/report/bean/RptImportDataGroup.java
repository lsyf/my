package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="charge.dim_sub_group")
public class RptImportDataGroup {
	
	private Long groupId;
	private Integer latnId;
	private String groupName;
	private Long subCode;
	private String subName;
	private Integer userId;
	private String lstUpd;
}
