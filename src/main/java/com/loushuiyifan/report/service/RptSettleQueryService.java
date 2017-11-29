package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.RptSettleQueryDAO;
import com.loushuiyifan.report.vo.SettleDataVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RptSettleQueryService {
    private static final Logger logger = LoggerFactory.getLogger(RptSettleQueryService.class);
    @Autowired
    RptSettleQueryDAO rptSettleQueryDAO;

    /**
     * 查询
     */
    public List<SettleDataVO> listSettle(String month, String reportId) {

        List<SettleDataVO> list = rptSettleQueryDAO.listData(month, reportId);
        
        return list;
    }
    
    public Map<String, Object> listDetail(Long logId,String incomeSource) {
//    	String[] Keys = { "reportId", "reportName", "acctMonth", "areaId", "areaName", "horCode", "verCode",
//    			"indexAllis", "incomeSource", "sourceName", "indexData" };
//    	String[] Values = { "报表编号", "报表名称", "账期", "地市编号", "地市名", "渠道编码", "指标编码", "指标名称", "收入来源编号", "收入来源名称", "数据" };
//
//    	List<Map<String, String>> titles =new ArrayList<>();
//    	for (int i = 0; i < Keys.length; i++) {
//			String key = Keys[i];
//			String value = Values[i];
//			Map<String, String> map = new HashMap<>();
//			map.put("ID", key);
//			map.put("NAME", value);
//			titles.add(map);
//		}
    	
    	
        List<Map<String, String>> titles = rptSettleQueryDAO.titleOld(logId);
        List<Map<String, String>> datas =rptSettleQueryDAO.listOld(logId);
       
        List<Map<String, String>> detail =rptSettleQueryDAO.detail(logId, incomeSource);
        
        Map<String, Object> map = Maps.newHashMap();
        map.put("datas", datas);
        map.put("titles", titles);
        map.put("detail", detail);
        
        return map;
    }

    /**
     * 报表编号查询
     */
    public List<Map<String, String>> listReportInfo() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> m = Maps.newHashMap();
        m.put("id", "0");
        m.put("name", "全部");
        m.put("data", "0");
        m.put("lvl", "1");
        list.add(m);

        List<Map<String, String>> list2 = rptSettleQueryDAO.listReportInfo();
        list.addAll(list2);
        return list;
    }


}
