package com.loushuiyifan.report.bean;

import java.util.Date;

import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name="RPT_SETT_CUT_LOG")
public class RptSettCutLog {
	 Long logId;
	 String fileName;
	 String acctMonth;
	 String detail;
	 Integer count;
	 Long userId;
	 Date importDate;
}
