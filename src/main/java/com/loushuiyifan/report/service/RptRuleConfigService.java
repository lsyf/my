package com.loushuiyifan.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.bean.CodeListTax;
import com.loushuiyifan.report.dao.RptRuleConfigDAO;
import com.loushuiyifan.report.vo.RuleConfigVO;

import oracle.net.aso.o;

@Service
public class RptRuleConfigService {
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
		
		List<CodeListTax> list2 = rptRuleConfigDAO.findNameById();
		
		 //然后拼接参数
        for (CodeListTax code: list2) {
        	String codeId = code.getCodeId();         	
            String path = code.getParentIds();
            path = path == null ? code + "/%" : path + code + "/%";
            code.setParentIds(path);
        }
        
		return list;
	}
}
