package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.vo.SettleDataVO;

public interface RptSettleOldDataDAO extends MyMapper<SettleDataVO>{
	List<Map<String, String>> titleOld(Long logId);
    List<Map<String, String>> listOld(Long logId);
	List<SettleDataVO> listData(@Param("month") String month,
                                @Param("reportId") String reportId);
}
