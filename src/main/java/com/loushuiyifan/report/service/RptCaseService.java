package com.loushuiyifan.report.service;

import com.loushuiyifan.report.bean.RptCase;
import com.loushuiyifan.report.dao.RptCaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 漏水亦凡
 * @date 2017/11/20
 */
@Service
public class RptCaseService {
    public static String TYPE_RptCust = "RptCust";
    public static String TYPE_RptIncomeSource = "RptIncomeSource";

    @Autowired
    RptCaseDAO rptCaseDAO;


    public Long saveCaseSelective(RptCase rptCase) {
        rptCaseDAO.insertSelective(rptCase);
        return rptCase.getRptCaseId();
    }

    public RptCase selectRptCustCase(String month, String latnId, String incomeSource, String tax) {
        return selectCase(month, latnId, incomeSource, null, tax, TYPE_RptCust);
    }

    public RptCase selectRptIncomeSourceCase(String month, String latnId, String cust, String tax) {
        return selectCase(month, latnId, null, cust, tax, TYPE_RptIncomeSource);
    }

    public RptCase selectCase(String month, String latnId, String incomeSource, String cust, String tax, String type) {
        return rptCaseDAO.selectCase(month, latnId, incomeSource, cust, tax, type);
    }
}
