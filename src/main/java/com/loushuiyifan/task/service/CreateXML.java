package com.loushuiyifan.task.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.task.bean.FileInfoData;
import com.loushuiyifan.task.bean.RptMsgInfoData;
import com.loushuiyifan.task.dao.RptMsgInfoDataDAO;

@Service
public class CreateXML {
	private static final Logger logger = LoggerFactory.getLogger(CreateXML.class);
	
	@Autowired
	RptMsgInfoDataDAO rptMsgInfoDataDAO;
	
	public Document createXML(RptMsgInfoData info, String month){
		InetAddress add;
		String address = null;
		String fileType = null;
		String rpt_type = "0";
		if ("DAPM_MSS_INCOME_YGZ_01".equals(info.getBatchId())) {
			fileType = "DAPM_MSS_INCOME_YGZ_01";
			rpt_type = "1";
		} else if ("DAPM_MSS_INCOME_YGZ_02".equals(info.getBatchId())) {
			fileType = "DAPM_MSS_INCOME_YGZ_02";
			rpt_type = "1";
		}else {
			fileType = "DAPM_MSS_INCOME";
			rpt_type = "0";
		}
		
		try {
			add = InetAddress.getLocalHost();
			address = add.getHostAddress().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Request");
		Element empContent = rootElement.addElement("content");
		Element empOH = empContent.addElement("Origin-Host");
		empOH.setText(info.getOriginHost());
		Element empDH = empContent.addElement("Destination-Host");
		empDH.setText(info.getDestHost());
		Element empBT = empContent.addElement("BusiType");
		Element empBT_BS = empBT.addElement("BusiService");
		empBT_BS.setText(info.getBusiService());
		Element empET = empContent.addElement("Event-Timestamp");
		empET.setText(info.getEventTime());
		Element empST = empContent.addElement("Service-Type");
		empST.setText("1020010");
		Element empDI = rootElement.addElement("Data-Information");
		Element empP = empDI.addElement("Path");
		empP.setText(info.getPath());
		Element empHI = empDI.addElement("HostIp");
		empHI.setText(info.getHostIp());
		Element empUN = empDI.addElement("UserName");
		empUN.setText(info.getUserName());
		Element empFL = empDI.addElement("FileList");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		FileInfoData temp = new FileInfoData();		
		temp.setFileType("DAT");
		temp.setRptType(rpt_type);
		temp.setFileDate(month);
		temp.setInterFaceType(info.getBatchId());
		
		List<FileInfoData> fin = rptMsgInfoDataDAO.queryFileInfo(temp);
		for(FileInfoData in : fin){
			StringBuilder sb = new StringBuilder();
			sb.append(fileType).append(".").append(sdf.format(Date.from(Instant.now()))).append(".")
			.append(in.getFileDate()).append(".").append(in.getNumber1()).append(".").append(in.getNumber2()).append(".")
			.append(in.getNumber3()).append(".833");
			String fileName = sb.toString();
			
			Element empFN1 = empFL.addElement("File_Name");
			empFN1.setText(fileName + ".DAT|" + in.getFileSize());
			info.setFileName(fileName + ".DAT");
			rptMsgInfoDataDAO.insertSelective(info);
			
		}
		
		FileInfoData temp2 = new FileInfoData();		
		temp2.setFileType("VAL");
		temp2.setRptType(rpt_type);
		temp2.setFileDate(month);
		temp2.setInterFaceType(info.getBatchId());
		
		List<FileInfoData> fin2 = rptMsgInfoDataDAO.queryFileInfo(temp2);
		for (FileInfoData in : fin2) {
			StringBuilder sb = new StringBuilder();
			sb.append(fileType).append(".").append(sdf.format(Date.from(Instant.now()))).append(".")
			.append(in.getFileDate()).append(".").append(in.getNumber1()).append(".").append(in.getNumber2()).append(".")
			.append(in.getNumber3()).append(".833");			
			String fileName = sb.toString();
			
			Element empFN1 = empFL.addElement("File_Name");
			empFN1.setText(fileName + ".VAL|" + in.getFileSize());
			info.setFileName(fileName + ".VAL");
			rptMsgInfoDataDAO.insertSelective(info);
		}
		
		return document;
	}
	
	
	
	
	
}
