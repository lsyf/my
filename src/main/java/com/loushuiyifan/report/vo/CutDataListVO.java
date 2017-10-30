package com.loushuiyifan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通用VO
 * @author YXK
 * @date 2017/10/27
 */
@Data
@AllArgsConstructor
public class CutDataListVO {
	 String ruleId;
	 Integer bureauId; // c5
	 Integer areaId; // c4
	 Double rate;
	 String acctMonth;	
	 String activeFlag;
	 String shareType;
	 String incomeSource;
	 String codeName;
	 String areaName;
	 String bureauName;
}
