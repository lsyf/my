package com.loushuiyifan.report.dao;

import java.util.List;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportCutRate;
import com.loushuiyifan.report.vo.CutDataListVO;
import com.loushuiyifan.report.vo.CutRateVO;

/**
 * 
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportCutRateDAO extends MyMapper<RptImportCutRate>{
	Double cutRateJihetype(String ruleId,String month);
	List<CutRateVO> cutRateJihetype2(String ruleId,String month);
	List<CutDataListVO> cutRateList(String month,
			Integer latnId,
            String incomeSource,
            Integer shareType,
            String type);
	void cutRateDel(Integer latnId,
            String incomeSource,
            Integer shareType,
            String userName,
            String activeFlag);
	
}
