package com.loushuiyifan.report.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.QueryIncomeStateDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.vo.TransLogVO;

@Service
public class QueryIncomeStateService {
	private static final Logger logger = LoggerFactory.getLogger(QueryIncomeStateService.class);
	
	@Autowired
	QueryIncomeStateDAO queryIncomeStateDAO;
	
	/**
     * 稽核数据
     */
    public List<TransLogVO> list(String month, 
		    		             String status) {

    	List<TransLogVO> list =queryIncomeStateDAO.queryLogList(month, status);
        return list;
    }
	
	/**
	 * 修改状态
	 * @param month
	 * @param subId
	 * @param userId
	 * @throws Exception
	 */
	public void changeState(String status, 
                            Long subId,
                            Long userId) {
		String name =queryIncomeStateDAO.checkUserId(userId);
		if(name ==null){
			throw new ReportException("该用户没有修改权限！");
		}
		
		queryIncomeStateDAO.updateState(status, subId);
	}
	
	
}
