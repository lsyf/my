package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataTax;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.dto.DeleteImportDataDTO;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;

	public interface RptImportDataTaxDAO extends MyMapper<RptImportDataTax> {
		void deleteTax(DeleteImportDataDTO dto);
		//导入校验
		void checkTaxData(SPDataDTO dto);
		//提交数据
		void pkgCutTaxData(SPDataDTO dto);
		//数据是否提交
		String checkPullStates(Long userId);
		
		//查询数据
		List<ImportLogDomTaxVO> listTax(@Param("userId") Long userId,
						                @Param("month") String month,
						                @Param("type") String type); 
		
		
		
		
		
	}
