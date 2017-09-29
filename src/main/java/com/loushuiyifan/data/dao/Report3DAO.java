package com.loushuiyifan.data.dao;


import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.data.vo.ProductConfig;
import com.loushuiyifan.data.vo.Report3;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface Report3DAO extends MyMapper<Report3> {


    /**
     * 查询指标配置
     *
     * @return
     */
    List<ProductConfig> listProductConfig();


    /**
     * 获取本月总和
     *
     * @param config
     * @param month
     * @return
     */
    BigDecimal getMonthTotal(@Param("config") String config,
                             @Param("month") String month);

    /**
     * 获取本年累计
     *
     * @param config
     * @return
     */
    BigDecimal getYearTotal(@Param("config") String config,
                            @Param("firstMonth") String firstMonth,
                            @Param("thisMonth") String thisMonth);


    BigDecimal getMonthIsee(String month);

    BigDecimal getYearIsee(@Param("firstMonth") String firstMonth,
                           @Param("thisMonth") String thisMonth);
}