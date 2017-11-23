package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.RptSettleQueryDAO;
import com.loushuiyifan.report.vo.SettleDataVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
