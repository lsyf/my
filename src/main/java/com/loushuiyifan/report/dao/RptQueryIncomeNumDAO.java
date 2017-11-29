package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.report.vo.RptIncomeNumVO;

public interface RptQueryIncomeNumDAO {

	List<RptIncomeNumVO> listdataForMap(@Param("month") String month, 
			                                 @Param("type") String type);
}
