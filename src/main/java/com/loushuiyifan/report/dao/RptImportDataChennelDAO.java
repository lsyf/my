package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataChennel;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.ImportDataLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface RptImportDataChennelDAO extends MyMapper<RptImportDataChennel> {


    void checkRptImportData(SPDataDTO dto);

    int deleteByLogId(Long logId);


    List<ImportDataLogVO> listIncomeDataLog(@Param("latnId") String latnId,
                                            @Param("month") String month,
                                            @Param("type") String type);

    String selectAction(Long logId);

    void iseeC4Cut(SPDataDTO dto);

    void commitRptImportData(SPDataDTO dto);

    void deleteImportData(SPDataDTO dto);


}
