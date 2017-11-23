package com.loushuiyifan.report.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataTax;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;

	public interface RptImportDataTaxDAO extends MyMapper<RptImportDataTax> {
		/**
		 * 删除存过
		 * IRPT_DEL_TAXDATA
		 * @param dto
		 */
		void deleteTax(SPDataDTO dto);
		
		/**
		 * 导入校验
		 * tax_data_check
		 * @param dto
		 */		
		void checkTaxData(SPDataDTO dto);
		
		//查询数据是否提交
		String checkPullStates(Long userId);
		
		//查询数据
		List<ImportLogDomTaxVO> listTax(@Param("userId") Long userId,
						                @Param("month") String month,
						                @Param("type") String type); 
		/**
		 * 提交
		 * PKG_CUT_TAXDATA.RPT_TAX_CUT
		 */
		void pkgCutTaxData(SPDataDTO dto);
		
		//生成税务
		 void insertTaxGroup();
		 
		 //查询批次号
		 ArrayList<String> selectBatchIds(String month);
	}
