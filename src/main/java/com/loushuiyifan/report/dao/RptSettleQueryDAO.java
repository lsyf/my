package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.report.vo.SettleDataVO;

public interface RptSettleQueryDAO {
	
	List<SettleDataVO> listData(@Param("month") String month, 
			                    @Param("reportId") String reportId);
	
	List<Map<String, String>> listReportName();
	
	
}
