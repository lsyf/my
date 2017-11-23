package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptSettCutRate;
import com.loushuiyifan.report.vo.SettCutRateVO;

public interface RptSettCutRateDAO extends MyMapper<RptSettCutRate>{

	void deleteData(Long logId);
	
	List<SettCutRateVO> SettCutRateData(@Param("month") String month,
			                            @Param("reportId") String reportId);
}
