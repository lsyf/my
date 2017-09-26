package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataChennel;
import com.loushuiyifan.report.dto.CheckDataDTO;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface RptImportDataChennelDAO extends MyMapper<RptImportDataChennel> {


    void checkRptImportData(CheckDataDTO dto);

    int deleteByLogId(Long logId);
}
