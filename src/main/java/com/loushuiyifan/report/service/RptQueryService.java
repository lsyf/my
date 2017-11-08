package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
import com.loushuiyifan.report.dao.RptQueryDAO;
import com.loushuiyifan.report.dao.RptRepfieldDefChannelDAO;
import com.loushuiyifan.report.dto.ReportDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
@Service
public class RptQueryService {


    @Autowired
    RptCustDefChannelDAO rptCustDefChannelDAO;

    @Autowired
    RptRepfieldDefChannelDAO rptRepfieldDefChannelDAO;

    @Autowired
    RptQueryDAO rptQueryDAO;


    public Map<String, Object> list(String month,
                                    String latnId,
                                    String incomeSource,
                                    String type) {

        //客户群
        List<Map> custs = rptCustDefChannelDAO.listMap("1701");
        //指标
        List<Map<String, String>> fields = rptRepfieldDefChannelDAO.listMap("1701");
        //数据
        List<ReportDataDTO> datas = rptQueryDAO.list(month, latnId, incomeSource, type);

        //首先遍历指标
        Map<String, Map<String, String>> fieldMap = Maps.newHashMapWithExpectedSize(3000);
        for (int i = 0; i < fields.size(); i++) {
            Map<String, String> temp = fields.get(i);
            fieldMap.put(temp.get("id"), temp);
        }

        //遍历数据，分别插入到指标数据中
        for (int i = 0; i < datas.size(); i++) {
            ReportDataDTO data = datas.get(i);
            String x = data.getX();
            Map<String, String> temp = fieldMap.get(x);
            if (temp == null) {
                continue;
            }

            String y = data.getY();
            String v = data.getV();
            temp.put(y, v);
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("titles", custs);
        result.put("datas", fields);
        return result;
    }
}
