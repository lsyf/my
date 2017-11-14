package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptIncomeSourceSts;

public interface QueryIncomeStateDAO extends MyMapper<RptIncomeSourceSts>{
	
	List<Map> queryLogList(@Param("month") String month, 
			               @Param("typeId") String typeId);
}
