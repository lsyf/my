package com.loushuiyifan.report.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptQueryIncomeNumDAO;
import com.loushuiyifan.report.vo.RptIncomeNumVO;

@Service
public class RptQueryIncomeNumService {

	 @Autowired
	 RptQueryIncomeNumDAO rptQueryIncomeNumDAO;
	
	
	public List<RptIncomeNumVO> list(String month, String type){
		
		List<RptIncomeNumVO> list =rptQueryIncomeNumDAO.listdataForMap(month,type);
		
		return list;
		
	}
	
	
	
	
	
	
	
}
