package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptIncomeSourceSts;
import com.loushuiyifan.report.vo.TransLogVO;

public interface QueryIncomeStateDAO extends MyMapper<RptIncomeSourceSts>{
	
	List<TransLogVO> queryLogList(@Param("month") String month, 
			                      @Param("status") String status);
	
	//String checkUserId(Long userId);
	
	void updateState(@Param("status") String status, 
                     @Param("subId") Long subId);
}
