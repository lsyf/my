package com.loushuiyifan.report.dao;

import java.util.List;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataCut;

/**
 * 
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportCutDataDAO extends MyMapper<RptImportDataCut>{
	List<RptImportDataCut> queryCut(String ruleId);
	void jihefaild(RptImportDataCut cut);
	
	List<String> checkCut(String month,
			Integer latnId,
            String incomeSource,
            Integer shareType,
            String userName,
            String ruleId);
	
	
}
