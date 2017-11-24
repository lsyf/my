package com.loushuiyifan.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.controller.RptQueryIncomeDetailController;
import com.loushuiyifan.report.dao.RptQueryIncomeDetailDAO;

@Service
public class RptQueryIncomeDetailService {
	private static final Logger logger = LoggerFactory.getLogger(RptQueryIncomeDetailService.class);
		
	
	@Autowired
	RptQueryIncomeDetailDAO rptQueryIncomeDetailDAO;
	
}
