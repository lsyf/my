package com.loushuiyifan.task.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="rpt_msg_info")
public class RptMsgInfoData {

	private Long msgId;
	private String batchId;
	private String sessionId;
	private String busiType;
	private String busiService;
	private String originHost;
	private String destHost;
	private String eventTime;
	private String path;
	private String hostIp;
	private String userName;
	private String fileName;
	private Integer resultCode;
}
