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
import org.springframework.web.bind.annotation.ResponseBody;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.RptSettleQueryService;
import com.loushuiyifan.report.service.RptFundsFeeQueryService;
import com.loushuiyifan.report.service.RptFundsFeeStatusService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.FundsStatusVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptFundsFeeStatus")
public class RptFundsFeeStatusController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptFundsFeeStatusController.class);
	@Autowired
	RptFundsFeeStatusService rptStatusFundsFeeFeeService;
	@Autowired
    RptFundsFeeQueryService rptFundsFeeQueryService;
	/**
     * 资金缴拨状态查询界面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:rptFundsFeeStatus:view")
    public String index(ModelMap map) {
        
        //页面条件
        List<CommonVO> months = dateService.lastMonths(2);
        List<Map<String, String>> reportIds = rptFundsFeeQueryService.listReportName();
        map.put("months", months);
        map.put("reportIds", reportIds);
        return "report/rptFundsFeeStatus";
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
    @RequiresPermissions("report:rptFundsFeeStatus:view")
    public JsonResult listQuery(String month, String reportId){
    	List<FundsStatusVO> list =rptStatusFundsFeeFeeService.list(month, reportId);
       
        return JsonResult.success(list);
    }

    /**
     * 回退
     */
    @PostMapping("quit")
    @ResponseBody
    @RequiresPermissions("report:rptFundsFeeStatus:quit")
    public JsonResult doQuit(String month, String reportId, @ModelAttribute("user") User user){
    
    	 Long userId = user.getId();
    	 
    		 rptStatusFundsFeeFeeService.quit(userId, month, reportId);
 		
        return JsonResult.success();
    }
    
    /**
     * 下载电子档案附件
     */
    @PostMapping("download")
    @ResponseBody
    @RequiresPermissions("report:rptFundsFeeStatus:view")
    public JsonResult loadFile(String month, String reportId){
    	//TODO

        return JsonResult.success();
    }
}