package com.loushuiyifan.report.service;

import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
import com.loushuiyifan.report.dao.RptRepfieldDefChannelDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/11/20
 */
@Service
public class RptEditionService {

    @Autowired
    RptCustDefChannelDAO rptCustDefChannelDAO;

    @Autowired
    RptRepfieldDefChannelDAO rptRepfieldDefChannelDAO;


    public List<Map<String, String>> listCustMap() {
        return rptCustDefChannelDAO.listMap("1701");
    }

    public List<Map<String, String>> listFieldMap() {
        return rptRepfieldDefChannelDAO.listMap("1701");
    }


}
