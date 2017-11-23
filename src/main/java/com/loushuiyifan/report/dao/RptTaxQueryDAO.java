package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface RptTaxQueryDAO {
	
	List<Map<String, String>> listData(@Param("month") String month,
										@Param("latnId")Integer latnId,
										@Param("report")String report);
}
