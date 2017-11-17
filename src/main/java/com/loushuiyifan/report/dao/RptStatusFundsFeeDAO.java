package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptBalanceData;
import com.loushuiyifan.report.vo.FundsStatusVO;

public interface RptStatusFundsFeeDAO extends MyMapper<RptBalanceData>{
	
	List<FundsStatusVO> listFundsFee(@Param("month") String month, 
			                         @Param("reportId") String reportId);
	
	
}
