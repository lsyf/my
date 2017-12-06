package com.loushuiyifan.report.bean;

import java.util.Date;

import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name="rpt_sett_cut_log")
public class RptSettCutLog {
	 Long logId;
	 String fileName;
	 String acctMonth;
	 String detail;
	 Integer count;
	 Long userId;
	 Date importDate;
}
