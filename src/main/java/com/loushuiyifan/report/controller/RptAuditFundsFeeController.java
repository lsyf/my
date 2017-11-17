package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptAuditFundsFeeService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.FundsAuditVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptAuditFundsFee")
public class RptAuditFundsFeeController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptAuditFundsFeeController.class);
	@Autowired
	RptAuditFundsFeeService RptAuditFundsFeeService;
	/**
     * 资金缴拨审核界面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map) {
        
        //页面条件
        List<Map<String, String>> reportIds =localNetService.listReportName();
        List<CommonVO> months = dateService.aroundMonths(5);       
        map.put("reportIds", reportIds);
        map.put("months", months);      
        return "report/rptAuditFundsFee";
    }

    /**
     * 报表查询
     * @param month
     * @param reportId
     * @param prctrName
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult listQuery(String month, String reportId){
    	List<FundsAuditVO> list =RptAuditFundsFeeService.list(month, reportId);
       

        return JsonResult.success(list);
    }

    /**
     * 报表审核
     */
    @PostMapping("audit")
    @ResponseBody
    public JsonResult auditReport(String month, String reportId){
        //TODO 报表审核未定
        try {
            RptAuditFundsFeeService.auditReport(month, reportId);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success();
    }
}
