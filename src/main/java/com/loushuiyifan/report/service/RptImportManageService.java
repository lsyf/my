package com.loushuiyifan.report.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptImportCutDataDAO;
import com.loushuiyifan.report.dao.RptImportManageDAO;
import com.loushuiyifan.report.exception.ReportException;

@Service
public class RptImportManageService {
	private static final Logger logger = LoggerFactory.getLogger(RptImportManageService.class);
	
	@Autowired
	RptImportManageDAO rptImportManageDAO;
	@Autowired
	RptImportCutDataDAO rptImportCutDataDAO;
	
	public List<Map<String,String>> list(String startDate,
                                         String endDate,
                                         String fileName,
                                         String userName,
                                         Long userId)throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	String start =null;
    	String end = null;
    	
    	if(sdf.parse(startDate).getTime() > sdf.parse(endDate).getTime()){
    		logger.info("开始时间大于结束时间");
         	start = endDate;
         	end = startDate;
    	}else if(sdf.parse(startDate).getTime() < sdf.parse(endDate).getTime()){
     		logger.info("#############=="+startDate);
         	logger.info("#############=="+endDate);
     		start = startDate;
     		end = endDate;
    	}else{
     		return null;
    	}
    	
    	List<Map<String,String>> list =null;
    	
    	List<String> latnIds = rptImportManageDAO.selectLatnByuserId(userId);
    	List<String> role =rptImportCutDataDAO.selectRoleById(userId);
    	if(role.contains("1")){
    		
    		list = rptImportManageDAO.listForMap(start, 
    				end, fileName, userName,"1",latnIds);
    	}else{
    		list = rptImportManageDAO.listForMap(start, 
    				end, fileName, userName,"2",latnIds);
    	}
		
		return list;
	}
}
