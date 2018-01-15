package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface RptImportManageDAO {
	List<String> selectLatnByuserId(Long userId);
	
	List<Map<String,String>> listForMap(@Param("startDate") String startDate,
										@Param("endDate") String endDate,
										@Param("fileName") String fileName,
										@Param("userName") String userName,
										@Param("flag") String flag,
										@Param("list") List<String> latnIds);
}
