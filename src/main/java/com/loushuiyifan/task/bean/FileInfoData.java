package com.loushuiyifan.task.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="file_info")
public class FileInfoData {

	private Long fileId;
	private String interFaceType;
	private String fileType;
	private String fileDate;
	private Long fileSize;
	private Integer fileNumber;
	private String number1;
	private String number2;
	private String number3;
	
	private String rptType;
	private String forFileType;
}
