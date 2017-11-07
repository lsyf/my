package com.loushuiyifan.report.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataTax;
import com.loushuiyifan.report.dto.CheckDataDTO;
import com.loushuiyifan.report.dto.DeleteImportDataDTO;
import com.loushuiyifan.report.dto.DeleteYccyDataDTO;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;

	public interface RptImportDataTaxDAO extends MyMapper<RptImportDataTax> {
		void deleteTax(DeleteImportDataDTO dto);
		//导入校验
		void checkTaxData(CheckDataDTO dto);
		//提交数据
		void pkgCutTaxData(CheckDataDTO dto);
		//数据是否提交
		String checkPullStates(Long userId);
		
		//查询数据
		List<ImportLogDomTaxVO> listTax(@Param("userId") Long userId,
						                @Param("month") String month,
						                @Param("type") String type); 
		
		//提交  
		void tijiaoTax(DeleteYccyDataDTO dto);
		
		//生成税务
		 void insertTaxGroup();
		 
		 //查询批次号
		 ArrayList<String> selectBatchIds(String month);
	}
