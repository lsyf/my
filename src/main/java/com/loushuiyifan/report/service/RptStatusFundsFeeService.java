package com.loushuiyifan.report.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptStatusFundsFeeDAO;
import com.loushuiyifan.report.vo.FundsStatusVO;

/**
 * 
 * @author yuxk
 * @date 2017-11-15
 */
@Service
public class RptStatusFundsFeeService {
	private static final Logger logger = LoggerFactory.getLogger(RptStatusFundsFeeService.class);
	@Autowired
	RptStatusFundsFeeDAO rptStatusFundsFeeDAO;
	
	/**
	 * 查询
	 */
	public List<FundsStatusVO> list(String month, String reportId){
		
		List<FundsStatusVO> list =rptStatusFundsFeeDAO.listFundsFee(month, reportId);
	
		return list;
	}
	
	
	
	
	
	
}
