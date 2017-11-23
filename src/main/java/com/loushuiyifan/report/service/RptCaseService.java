package com.loushuiyifan.report.service;

import com.loushuiyifan.config.mybatis.BaseService;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.ReportCache;
import com.loushuiyifan.report.bean.RptCase;
import com.loushuiyifan.report.dao.ReportCacheDAO;
import com.loushuiyifan.report.dao.RptCaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 漏水亦凡
 * @date 2017/11/20
 */
@Service
public class RptCaseService extends BaseService<RptCase> {

    @Autowired
    RptCaseDAO rptCaseDAO;

    @Autowired
    ReportCacheDAO reportCacheDAO;

    public int saveReportCache(Long rptCaseId,
                               byte[] htmlData,
                               String filePath) {
        ReportCache cache = new ReportCache();
        cache.setRptCaseId(rptCaseId);
        cache.setHtmlData(htmlData);
        cache.setFilePath(filePath);
        return reportCacheDAO.insertSelective(cache);

    }

    public void removeCache(Long rptCaseId) {
        rptCaseDAO.deleteByPrimaryKey(rptCaseId);
        reportCacheDAO.deleteByPrimaryKey(rptCaseId);
    }

    public Long saveCaseSelective(RptCase rptCase) {
        rptCaseDAO.insertSelective(rptCase);
        return rptCase.getRptCaseId();
    }

    public RptCase selectRptCustCase(String month, String latnId, String incomeSource, String tax) {
        return selectCase(month, latnId, incomeSource, null, tax,
                ReportConfig.RptExportType.RPT_QUERY_CUST.toString());
    }

    public RptCase selectRptIncomeSourceCase(String month, String latnId, String cust, String tax) {
        return selectCase(month, latnId, null, cust, tax,
                ReportConfig.RptExportType.RPT_QUERY_INCOME_SOURCE.toString());
    }

    public RptCase selectRptComparedNumCase(String month, String latnId, String tax) {
        return selectCase(month, latnId, null, null, tax,
                ReportConfig.RptExportType.RPT_QUERY_COMPARED_NUM.toString());
    }

    public RptCase selectCase(String month, String latnId, String incomeSource, String cust, String tax, String type) {
        return rptCaseDAO.selectCase(month, latnId, incomeSource, cust, tax, type);
    }
}
