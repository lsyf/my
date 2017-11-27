package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptExcelWyf;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/11/24.
 */
public interface RptQueryCreateDAO extends MyMapper<RptExcelWyf> {
    List<RptExcelWyf> list(@Param("month") String month,
                           @Param("latnId") String latnId,
                           @Param("incomeSource") String incomeSource);

    void remove(@Param("excelIds") Long[] excelIds);
}
