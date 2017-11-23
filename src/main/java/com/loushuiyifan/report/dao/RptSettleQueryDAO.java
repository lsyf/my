package com.loushuiyifan.report.dao;

import com.loushuiyifan.report.vo.SettleDataVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RptSettleQueryDAO {

    List<SettleDataVO> listData(@Param("month") String month,
                                @Param("reportId") String reportId);

    List<Map<String, String>> listReportInfo();


}
