package com.loushuiyifan.task.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="file_info")
public class FileXmlLogData {
	private Integer logId;
	private String sessionId;
	private String allXml;
	private String createTime;
}
