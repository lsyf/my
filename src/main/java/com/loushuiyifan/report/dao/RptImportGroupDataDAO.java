package com.loushuiyifan.report.dao;

import java.util.List;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataGroup;

/**
 * 
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportGroupDataDAO extends MyMapper<RptImportDataGroup>{
	
	void delete(Integer latnId, Long groupId);
	List<String> findSubcode(Long subcode);
	
	
}
