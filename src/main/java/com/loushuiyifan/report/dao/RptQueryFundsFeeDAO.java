package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptBalanceData;
import com.loushuiyifan.report.vo.FundsFeeVO;

public interface RptQueryFundsFeeDAO extends MyMapper<RptBalanceData>{
	
	List<FundsFeeVO> listFundsFee(@Param("month") String month, 
			                      @Param("reportId") String reportId,
			                      @Param("prctrName") String prctrName);
	
	
}
