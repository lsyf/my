package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataC5;
import com.loushuiyifan.report.dto.CheckDataDTO;
import com.loushuiyifan.report.dto.DeleteImportDataDTO;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface RptImportDataC5DAO extends MyMapper<RptImportDataC5> {

	void checkC5Data(CheckDataDTO dto);
	void deleteImportData(DeleteImportDataDTO dto);
	
	//稽核
	
}
