package com.loushuiyifan.report.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptSettleERecordDAO;
import com.loushuiyifan.report.vo.SettleDataVO;

@Service
public class RptSettleERecordService {
	private static final Logger logger = LoggerFactory.getLogger(RptSettleQueryService.class);
	@Autowired
	RptSettleERecordDAO rptSettleERecordDAO;
	
	/**
	 * 查询
	 */
	public List<SettleDataVO>  listSettle(String month){
		
		List<SettleDataVO> list =rptSettleERecordDAO.listData(month);
		
		return list;
	}
	
}
