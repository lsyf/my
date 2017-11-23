package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportYccyData;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;

public interface RptImportYccyDataDAO extends MyMapper<RptImportYccyData>{
	
	/**
	 * 删除存过
	 * ssc_ychd_del_data
	 * @param dto
	 */
	void deleteImportData(SPDataDTO dto);
	 
	 /**
	  *校验存过 
	  * ssc_ychd_check
	  * @param dto
	  */
	 void checkYCCYData(SPDataDTO dto);
	 
	 
	 List<ImportLogDomTaxVO> listDataLog(@Param("userId") Long userId,
							           @Param("month") String month
							           );

	 
	 
	 
	 
}
