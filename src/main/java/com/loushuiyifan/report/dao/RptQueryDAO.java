package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.dto.ReportDataDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
public interface RptQueryDAO extends MyMapper<CommonVO> {

    List<ReportDataDTO> list(@Param("month") String month,
                             @Param("incomeSource") String incomeSource,
                             @Param("latnId")  String latnId,
                             @Param("type")  String type);

}
