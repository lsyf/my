package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataTax;
import com.loushuiyifan.report.dto.CheckDataDTO;
import com.loushuiyifan.report.dto.DeleteImportDataDTO;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;

	public interface RptImportDataTaxDAO extends MyMapper<RptImportDataTax> {
		void deleteTax(DeleteImportDataDTO dto);
		void checkTaxData(CheckDataDTO dto);
		String checkPullStates(Long userId);
		void selectTax(CheckDataDTO dto);
		
		List<ImportLogDomTaxVO> listTax(@Param("userId") Long userId,
						                @Param("month") String month,
						                @Param("type") String type); 
		
		
		
		
		
	}
