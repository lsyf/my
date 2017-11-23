package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.CodeListTaxDAO;
import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
import com.loushuiyifan.report.dao.RptRepfieldDefChannelDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List<Map<String, String>> listCustMap() {
        return rptCustDefChannelDAO.listMap("1701");
    }

    public List<Map<String, String>> listFieldMap() {
        return rptRepfieldDefChannelDAO.listMap("1701");
    }

    public List<Map<String, String>> listIncomeSourceMap() {
        return codeListTaxDAO.listIncomeSourceMap("income_source2017");
    }

    public List<Map<String, String>> listComeparedNumMap() {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map1 = Maps.newHashMap();
        map1.put("id", "0_1");
        map1.put("name", "上年同期累计数");
        Map<String, String> map2 = Maps.newHashMap();
        map2.put("id", "0_2");
        map2.put("name", "本月发生数");
        Map<String, String> map3 = Maps.newHashMap();
        map3.put("id", "0_3");
        map3.put("name", "本年累计数");
        list.add(map1);
        list.add(map2);
        list.add(map3);
        return list;
    }
}
