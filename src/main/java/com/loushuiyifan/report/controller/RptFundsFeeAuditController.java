package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.RptFundsFeeAuditService;
import com.loushuiyifan.report.service.RptFundsFeeQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.FundsAuditVO;
import com.loushuiyifan.system.vo.JsonResult;

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
        List<CommonVO> months = dateService.commonMonths();
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

    @PostMapping("listAudit")
    @ResponseBody
    public JsonResult listAudit(String month,String reportId) {
    	
    	Map<String, Object>  map = rptFundsFeeAuditService.listAudit(month,reportId);
           	
        return JsonResult.success(map);
    }
    
    /**
     * 报表审核
     */
    @PostMapping("audit")
    @ResponseBody
    public JsonResult auditReport(String rptCaseId, String status, String comment,
                                  @ModelAttribute("user") User user) {
    	Long userId = user.getId();
    	try {
    		rptFundsFeeAuditService.audit(rptCaseId, status, comment, userId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ReportException("提示"+e.getMessage());
		}
    	
    	return JsonResult.success();
    }
}
