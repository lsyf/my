package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportYccyData;
import com.loushuiyifan.report.dto.DeleteYccyDataDTO;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;

public interface RptImportYccyDataDAO extends MyMapper<RptImportYccyData>{
	 void deleteImportData(DeleteYccyDataDTO dto);
	 
	 
	 List<ImportLogDomTaxVO> listDataLog(@Param("userId") Long userId,
							           @Param("month") String month
							           );

	 
	 
	 
	 
}
