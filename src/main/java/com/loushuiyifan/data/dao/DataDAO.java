package com.loushuiyifan.data.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.data.vo.DataAnalysis;
import com.loushuiyifan.data.vo.MonthData;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
public interface DataDAO extends MyMapper<DataAnalysis> {

    List<MonthData> da1();

    List<DataAnalysis> da2(String month);
}
