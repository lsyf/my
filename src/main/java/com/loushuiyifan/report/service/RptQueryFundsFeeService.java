package com.loushuiyifan.report.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptQueryFundsFeeDAO;
import com.loushuiyifan.report.vo.FundsFeeVO;

/**
 * 
 * @author yuxk
 * @date 2017-11-15
 */
@Service
public class RptQueryFundsFeeService {
	private static final Logger logger = LoggerFactory.getLogger(RptQueryFundsFeeService.class);
	@Autowired
	RptQueryFundsFeeDAO rptQueryFundsFeeDAO;
	
	/**
	 * 查询
	 */
	public List<FundsFeeVO> list(String month, 
                                 String reportId,
                                 String prctrName){
		
		List<FundsFeeVO> list =rptQueryFundsFeeDAO.listFundsFee(month, reportId, prctrName);
	
		return list;
	}
	
	
	
	
	
	
}
