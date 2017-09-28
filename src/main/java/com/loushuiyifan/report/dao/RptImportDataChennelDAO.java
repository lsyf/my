package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataChennel;
import com.loushuiyifan.report.dto.CheckDataDTO;
import com.loushuiyifan.report.dto.DeleteImportDataDTO;
import com.loushuiyifan.report.dto.IseeC4CutDTO;
import com.loushuiyifan.report.vo.IncomeDataLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface RptImportDataChennelDAO extends MyMapper<RptImportDataChennel> {


    void checkRptImportData(CheckDataDTO dto);

    int deleteByLogId(Long logId);


    List<IncomeDataLogVO> listIncomeDataLog(@Param("userId") Long userId,
                                            @Param("month") String month,
                                            @Param("type") String type);

    String selectAction(Long logId);

    void iseeC4Cut(IseeC4CutDTO dto);

    void commitRptImportData(CheckDataDTO dto);

    void deleteImportData(DeleteImportDataDTO dto);


}
