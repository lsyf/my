package com.loushuiyifan.report.vo;

import javax.persistence.Table;

import lombok.Data;

@Data
public class ImportDataGroupVO {
	
	 Long groupId;
	 Integer latnId;
	 String groupName;
	 Long subCode;
	 String subName;
	 Integer userId;
	 String lstUpd;
}
