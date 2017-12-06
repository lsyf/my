package com.loushuiyifan.report.controller;

import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptFundsFeeAuditService;
import com.loushuiyifan.report.service.RptFundsFeeQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.FundsAuditVO;
import com.loushuiyifan.system.vo.JsonResult;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("rptFundsFeeAudit")
public class RptFundsFeeAuditController extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(RptFundsFeeAuditController.class);
    @Autowired
    RptFundsFeeAuditService rptFundsFeeAuditService;
    @Autowired
    RptFundsFeeQueryService rptFundsFeeQueryService;

    /**
     * 资金缴拨审核界面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:rptFundsFeeAudit:view")
    public String index(ModelMap map) {

        //页面条件
        List<Map<String, String>> reportIds = rptFundsFeeQueryService.listReportName();
        List<CommonVO> months = dateService.aroundMonths(5);
        map.put("reportIds", reportIds);
        map.put("months", months);
        return "report/rptFundsFeeAudit";
    }

    /**
     * 报表查询
     *
     * @param month
     * @param reportId
     * @param prctrName
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:rptFundsFeeAudit:view")
    public JsonResult listQuery(String month, String reportId) {
        List<FundsAuditVO> list = rptFundsFeeAuditService.list(month, reportId);


        return JsonResult.success(list);
    }

    /**
     * 报表审核
     */
    @PostMapping("audit")
    @ResponseBody
    public JsonResult auditReport(String month, String reportId) {
        //TODO 报表审核未定
        try {
            rptFundsFeeAuditService.auditReport(month, reportId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success();
    }
}
