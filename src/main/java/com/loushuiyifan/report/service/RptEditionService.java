package com.loushuiyifan.report.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.CodeListTaxDAO;
import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
import com.loushuiyifan.report.dao.RptQueryComDetailDAO;
import com.loushuiyifan.report.dao.RptQueryComDetailDAO2017;
import com.loushuiyifan.report.dao.RptRepfieldDefChannelDAO;

/**
 * 报表数据版本
 * todo 待用来根据年份控制rpt_no
 *
 * @author 漏水亦凡
 * @date 2017/11/20
 */
@Service
public class RptEditionService {

    @Autowired
    RptCustDefChannelDAO rptCustDefChannelDAO;

    @Autowired
    RptRepfieldDefChannelDAO rptRepfieldDefChannelDAO;

    @Autowired
    CodeListTaxDAO codeListTaxDAO;

    @Autowired
    RptQueryComDetailDAO rptQueryComDetailDAO;
    @Autowired
    RptQueryComDetailDAO2017 rptQueryComDetailDAO2017;
    public static List<Map<String, String>> list_comparedNum;
    public static List<Map<String, String>> list_comDetail_col;

    static {
        String[] ids = {"0_1", "0_2", "0_3"};
        String[] names = {"汇总-上年同期累计数", "汇总-本月发生数", "汇总-本年累计数"};
        list_comparedNum = generateColMap(ids, names);

        String[] ids2 = {"1", "2", "3"};
        String[] names2 = {"上年同期数", "本月发生数", "本年累计数"};
        list_comDetail_col = generateColMap(ids2, names2);
    }

    private static List<Map<String, String>> generateColMap(String[] ids, String[] names) {
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            Map<String, String> map = Maps.newHashMap();
            map.put("id", ids[i]);
            map.put("name", names[i]);
            list.add(map);
        }
        return list;
    }
    
    /**
     * 2017年指标
     * @return
     */
    public List<Map<String, String>> listFieldMap2017() {
        return rptRepfieldDefChannelDAO.listMap("1701");
    }
    /**
     * 2017年收入来源
     * @return
     */
    public List<Map<String, String>> listIncomeSourceMap2017() {
        return codeListTaxDAO.listIncomeSourceMap("income_source2017");
    }
    /**
     * 2017客户群    
     * @return
     */
    public List<Map<String, String>> listCustMap2017() {
        return rptCustDefChannelDAO.listMap("1701");
    }
    /**
     * 2017年通信主页明细列名
     * @return
     */
    public List<Map<String, String>> listComDetailColMap2017() {            	    	
    	return list_comDetail_col;
    }
    /**
     * 2017年通信主页明细指标
     * @return
     */
    public List<Map<String, String>> listComDetailRowMap2017() {
        return rptQueryComDetailDAO2017.listComDetailRowMap("1701");
    }
    
    public List<Map<String, String>> listCustMap() {
        return rptCustDefChannelDAO.listMap("1801");
    }

    public List<Map<String, String>> listFieldMap() {
        return rptRepfieldDefChannelDAO.listMap("1801");
    }

    public List<Map<String, String>> listIncomeSourceMap() {
        return codeListTaxDAO.listIncomeSourceMap("income_source2018");
    }

    public List<Map<String, String>> listComeparedNumMap() {
        return list_comparedNum;
    }
   
    public List<Map<String, String>> listComDetailColMap(){
    	List<Map<String, String>> cols = new ArrayList<>();
    	
    	String[] id = {"1_", "2_", "3_"};
        String[] name = {"-上年同期数", "-本月发生数", "-本年累计数"};
        List<Map<String, String>> list =rptQueryComDetailDAO.selectIndexCodeAndName("1801");
    	for (int k = 0; k < list.size(); k++) {
    		Map<String, String> map = list.get(k);
    		   		
			String[] codes = new String[3];
			String[] names = new String[3];
			for(int i = 0; i < id.length; i++){								
				codes[i] = (id[i]+map.get("id")).toString();
				names[i] = (map.get("name")+name[i]).toString();
				
				Map<String, String> temp = new LinkedHashMap<>();
				temp.put("id", codes[i]);
				temp.put("name", names[i]);
		        cols.add(temp);		        
			}						
		}
    	
    	return cols;
    }

    
    public List<Map<String, String>> listComDetailRowMap() {
        return rptQueryComDetailDAO.listComDetailRowMap("1801");
    }
}
