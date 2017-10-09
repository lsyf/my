package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataICT;
import com.loushuiyifan.report.dto.CheckDataDTO;
import com.loushuiyifan.report.dto.DeleteImportDataDTO;
import com.loushuiyifan.report.vo.ImportDataLogVO;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface RptImportDataICTDAO extends MyMapper<RptImportDataICT> {

    void checkImportData(CheckDataDTO dto);

    void deleteImportData(DeleteImportDataDTO dto);

    List<ImportDataLogVO> listICTLog(Long userId, String month, String type);
}