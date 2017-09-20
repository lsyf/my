package com.loushuiyifan.da.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.da.vo.DataAnalysis;
import com.loushuiyifan.da.vo.MonthData;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
public interface DaDAO extends MyMapper<DataAnalysis> {

    List<MonthData> da1();

    List<DataAnalysis> da2(String month);
}
