package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.SettleDataVO;

public interface RptSettleQueryDAO extends MyMapper<SettleDataVO>{

    List<SettleDataVO> listData(@Param("month") String month,
                                @Param("reportId") String reportId);
    
    List<Map<String, String>> titleOld(Long logId);
    List<Map<String, String>> listOld(Long logId);
    
    List<Map<String, String>> detail(@Param("logId") Long logId,
    		                           @Param("incomeSource") String incomeSource);
    
    List<Map<String, String>> listReportInfo();

    void selectAudits(Map map);
    
    void auditRpt(SPDataDTO dto);
}
