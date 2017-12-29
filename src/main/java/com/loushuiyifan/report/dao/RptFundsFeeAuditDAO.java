package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptBalanceData;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.FundsAuditVO;

public interface RptFundsFeeAuditDAO extends MyMapper<RptBalanceData>{
	
	List<FundsAuditVO> listFundsFee(@Param("month") String month, 
			                        @Param("reportId") String reportId);
	
	void selectAudits(Map map);
    
    void auditRpt(SPDataDTO dto);
}
