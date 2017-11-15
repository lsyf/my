package com.loushuiyifan.report.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.QueryIncomeStateDAO;

@Service
public class QueryIncomeStateService {
	private static final Logger logger = LoggerFactory.getLogger(QueryIncomeStateService.class);
	
	@Autowired
	QueryIncomeStateDAO queryIncomeStateDAO;
	
	/**
     * 稽核数据
     */
    public List<Map> list(String month, 
		    		      String typeId) {

        //String type = ReportConfig.RptImportType.C5.toString();
        List<Map> list =queryIncomeStateDAO.queryLogList(month, typeId);
        return list;
    }
	
	
	
	
	
}
