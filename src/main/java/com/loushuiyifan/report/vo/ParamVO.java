package com.loushuiyifan.report.vo;

import java.util.List;

public class ParamVO {
	String month;
	List<SettleDataVO> logs;
	
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public List<SettleDataVO> getLogs() {
		return logs;
	}
	public void setLogs(List<SettleDataVO> logs) {
		this.logs = logs;
	}
	
	
}
