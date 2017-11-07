package com.loushuiyifan.report.service;

import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
import com.loushuiyifan.report.dao.RptQueryDAO;
import com.loushuiyifan.report.dao.RptRepfieldDefChannelDAO;
import com.loushuiyifan.report.exception.ReportException;
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
        List<Map> fields = rptRepfieldDefChannelDAO.listMap("1701");


        switch (type) {
            case "0":
                break;
            case "1":
                break;
            case "2":
                break;
            default:
                throw new ReportException("无法确认税务类型");
        }

        return null;
    }
}
