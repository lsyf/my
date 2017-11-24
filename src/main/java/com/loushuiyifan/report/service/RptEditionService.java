package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.CodeListTaxDAO;
import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
import com.loushuiyifan.report.dao.RptQueryComDetailDAO;
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

    @Autowired
    RptQueryComDetailDAO rptQueryComDetailDAO;

    public static List<Map<String, String>> list_comparedNum;
    public static List<Map<String, String>> list_comDetail_col;

    static {
        String[] ids = {"0_1", "0_2", "0_3"};
        String[] names = {"上年同期累计数", "本月发生数", "本年累计数"};
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
        return list_comparedNum;
    }

    public List<Map<String, String>> listComDetailColMap() {
        return list_comDetail_col;
    }

    public List<Map<String, String>> listComDetailRowMap() {
        return rptQueryComDetailDAO.listComDetailRowMap("1701");
    }
}
