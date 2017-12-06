package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface RptImportManageDAO {

	List<Map<String,String>> listForMap(@Param("startDate") String startDate,
										@Param("endDate") String endDate,
										@Param("fileName") String fileName,
										@Param("user") String user,
										@Param("userId") Long userId);
}
