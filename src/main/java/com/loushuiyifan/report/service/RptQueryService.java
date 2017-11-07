package com.loushuiyifan.report.service;

import com.loushuiyifan.report.bean.RptCustDefChannel;
import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
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


    public Map<String, Object> list(String month,
                                    String latnId,
                                    String incomeSource,
                                    String type) {

       List<RptCustDefChannel> titles =  rptCustDefChannelDAO.list("1701");


        return null;
    }
}
