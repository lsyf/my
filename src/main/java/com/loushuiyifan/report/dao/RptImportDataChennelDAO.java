package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataChennel;
import com.loushuiyifan.report.dto.CheckDataDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface RptImportDataChennelDAO extends MyMapper<RptImportDataChennel> {


    int addRptImportDatas(@Param("logId") Long logId,
                          @Param("datas") List<RptImportDataChennel> datas);

    void checkRptImportData(CheckDataDTO dto);

    int deleteByLogId(Long logId);
}
