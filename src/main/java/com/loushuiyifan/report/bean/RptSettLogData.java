package com.loushuiyifan.report.bean;

import java.util.Date;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="rpt_sett_log")
public class RptSettLogData {
	Long logId;      //报表编码
	String fileName; //文件名
	String batchId;  //批次号
	Integer fileSeq;  //重传次数
	String acctMonth; //账期
	Date creageDate;  //下发时间
	Date importDate;  //加载时间
	String state;     //状态(R:采集成功待入库  Y: 成功入库   F:入库出错  f:校验失败  N：定义表无该批次)
}
