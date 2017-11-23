package com.loushuiyifan.report.bean;

import java.util.Date;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="rpt_sett_state")
public class RptSettStateData {
	String reportId; //报表编码
	String acctMonth; //账期
	String state;    //状态（0：无或未关联数据 1：已关联数据  2：非预期下发文件）
	Long logId;  //日志编号
	Date lstUpd; //最后修改时间



}
