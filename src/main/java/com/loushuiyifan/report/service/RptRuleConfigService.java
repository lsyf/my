package com.loushuiyifan.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.bean.CodeListTax;
import com.loushuiyifan.report.controller.RptFundsFeeQueryController;
import com.loushuiyifan.report.dao.RptRuleConfigDAO;
import com.loushuiyifan.report.vo.RuleConfigVO;

import oracle.net.aso.o;

@Service
public class RptRuleConfigService {
	private static final Logger logger = LoggerFactory.getLogger(RptRuleConfigService.class);
	@Autowired
	RptRuleConfigDAO rptRuleConfigDAO;
	
	
	/**
	 * 查询
	 * @param month
	 * @param latnId
	 * @return
	 */
	public List<RuleConfigVO> list(String month,String latnId, String cardType,
                                   String discount, Long logId){
		
		List<RuleConfigVO> list = rptRuleConfigDAO.listData(month, latnId, cardType, 
				                                            logId, discount);
		
		return list;
	}
	
	
	/**
	 * 查询卡的类型
	 */
	public List<Map<String,String>> listCard(){
		
		List<Map<String,String>> list = new ArrayList<>();
		Map<String,String> map = Maps.newHashMap();
		map.put("id", "0");
		map.put("name", "全部");
		map.put("data", "0");
		map.put("lvl", "1");
		list.add(map);
		
		List<Map<String,String>> list2 = rptRuleConfigDAO.findCardName();
		
		list.addAll(list2);
	return list;
	}
	
    public List<Map<String,String>> getCodeName(){
		
		List<Map<String,String>> list = new ArrayList<>();
		Map<String,String> map = Maps.newHashMap();
		map.put("id", "0");
		map.put("name", "全部");
		map.put("data", "0");
		map.put("lvl", "1");
		list.add(map);
		
		List<String> temp = rptRuleConfigDAO.queryNameForMap();
		
		List<Map<String,String>> list2 = rptRuleConfigDAO.findNameById(temp);
		list.addAll(list2);
		return list;
	}
}
