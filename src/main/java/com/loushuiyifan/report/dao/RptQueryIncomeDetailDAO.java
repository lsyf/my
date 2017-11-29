package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.report.vo.IncomeDetailVO;

public interface RptQueryIncomeDetailDAO {
	List<IncomeDetailVO> listIncomeData(@Param("startDate") String startDate,
			                            @Param("endDate") String endDate,
			                            @Param("state") String state);
	
	List<Map<String,String>> detailById(String sessionId);
	
	List<IncomeDetailVO>findDataById(String sessionId);
	String selectLstUpd(String sessionId);
	
	void updateById(String sessionId);
	Map<String,String> selectCodeById(String sessionId);
}
