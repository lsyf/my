package com.loushuiyifan.report.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.controller.RptQueryIncomeDetailController;
import com.loushuiyifan.report.dao.RptImportManageDAO;

@Service
public class RptImportManageService {
	private static final Logger logger = LoggerFactory.getLogger(RptImportManageService.class);
	
	@Autowired
	RptImportManageDAO rptImportManageDAO;
	
	
	public List<Map<String,String>> list(String startDate,
                                         String endDate,
                                         String fileName,
                                         String userId,
                                         Long userIds)throws Exception{
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
    	
		List<Map<String,String>> list = rptImportManageDAO.listForMap(start, 
				end, fileName, userId,userIds);
		
		
		return list;
	}
}
