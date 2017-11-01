package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="dim_sub_group" ,schema="charge")
public class RptImportDataGroup {
	
	 Long groupId;
	 Integer latnId;
	 String groupName;
	 Long subCode;
	 String subName;
	 Integer userId;
	 String lstUpd;
}
