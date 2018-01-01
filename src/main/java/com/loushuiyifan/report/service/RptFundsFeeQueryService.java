package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.RptFundsFeeQueryDAO;
import com.loushuiyifan.report.serv.CommonExportServ;
import com.loushuiyifan.report.vo.FundsFeeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yuxk
 * @date 2017-11-15
 */
@Service
public class RptFundsFeeQueryService {
    private static final Logger logger = LoggerFactory.getLogger(RptFundsFeeQueryService.class);
    @Autowired
    RptFundsFeeQueryDAO rptFundsFeeQueryDAO;

    /**
     * 查询
     */
    public List<FundsFeeVO> list(String month, String reportId, String prctrName) {

        List<FundsFeeVO> list = rptFundsFeeQueryDAO.listFundsFee(month, reportId, prctrName);

        return list;
    }

    /**
     * 导出数据
     */
    public byte[] export(String month, String reportId, String prctrName) throws Exception {

        List<Map<String, String>> list = rptFundsFeeQueryDAO.queryLogForMap(month, reportId, prctrName);

        String[] keys = {"indexCode", "indexName", "balance", "prctr", "prctrName", "sapFinCode"};

        String[] titles = {"指标编码", "指标名称", "金额", "利润中心编码", "利润中心简称", "SAP科目编码"};

        byte[] data = new CommonExportServ().column(keys, titles)
                .data(list)
                .exportData();

        return data;
    }


    public List<Map<String, String>> listReportName() {
        
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> m = Maps.newHashMap();
        m.put("id", "0");
        m.put("name", "全部");
        m.put("data", "0");
        m.put("lvl", "1");
        list.add(m);
        list.addAll(rptFundsFeeQueryDAO.listReportName());
        
        return list;
    }

}
