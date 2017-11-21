package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="rpt_sett_def")
public class RptSettDefData {
	String reportId;   //报表编码
	String reportName; //报表名称
	String batchId;    //文件批次号
	String incomeSource; //收入来源
	String settType;     //报表类型
	String areaLvl;      //C2 省级数据 C3 本地网级数据
	String cutFlag;      //
	String dataSource;
	String auditDepartment;
	String auditor;
	String parentReportId;
	String remark;
}
