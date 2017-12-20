package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.CodeListTax;
import com.loushuiyifan.report.vo.RuleConfigVO;

public interface RptRuleConfigDAO extends MyMapper<RuleConfigVO>{

	List<RuleConfigVO> listData(@Param("month") String month,
			                    @Param("latnId") String latnId, 
			                    @Param("cardType") String cardType,
			                    @Param("logId") Long logId,
			                    @Param("discount") String discount);
	
	
	List<Map<String,String>> findCardName();
	
	List<Organization> listAll();
	List<Organization> listByUserAndLvl(@Param("userId") Long userId,
                                        @Param("lvl") Integer lvl);
	
	List<Organization> listByRootAndLvl(@Param("id") Long id);
	
}
