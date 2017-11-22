package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.dto.SPDataDTO;

/**
 * @author 漏水亦凡
 * @date 2017/11/21
 */
public interface IncomeStatisticsDAO extends MyMapper<SPDataDTO>{
    void allSum(SPDataDTO dto);
}
