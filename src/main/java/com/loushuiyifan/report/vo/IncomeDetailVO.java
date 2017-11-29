package com.loushuiyifan.report.vo;

import lombok.Data;

@Data
public class IncomeDetailVO {
	String sessionId;   //会话号
	String beforeCode;  //采集前回执
	String afterCode;   //采集后回执
	String enterCode;   //入库回执
	String startDate;   //开始时间
	String endDate;     //结束时间
	
}
