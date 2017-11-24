package com.loushuiyifan.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.RptTaxQueryDAO;

@Service
public class RptTaxQueryService {
	private static final Logger logger = LoggerFactory.getLogger(RptTaxQueryService.class);

	@Autowired
	RptTaxQueryDAO rptTaxQueryDAO;
	
	
	/**
	 * 查询
	 */
	public List<Map<String,String>>  listSettle(String month,Integer latnId,String report){
		
		List<Map<String,String>> list =rptTaxQueryDAO.listData(month, latnId, report);
		
		return list;
	}
	
	
	public List<Map<String,String>> listAreaInfo(){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> m = Maps.newHashMap();
        m.put("id", "0");
        m.put("name", "全部");
        m.put("data", "0");
        m.put("lvl", "1");
        list.add(m);

        List<Map<String, String>> list2 = rptTaxQueryDAO.listAreaForTax();
        list.addAll(list2);
		return list;
	}
}
