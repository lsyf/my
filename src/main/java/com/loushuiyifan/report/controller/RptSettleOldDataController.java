package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.RptSettleOldDataService;
import com.loushuiyifan.report.service.RptSettleQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.ParamVO;
import com.loushuiyifan.report.vo.SettleDataVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptSettleOldData")
public class RptSettleOldDataController extends BaseReportController{

	private static final Logger logger = LoggerFactory.getLogger(RptSettleOldDataController.class);
	@Autowired
	RptSettleOldDataService rptSettleOldDataService;
	@Autowired
	RptSettleQueryService rptSettleQueryService;
	
	/**
     * 集团结算原始数据查询界面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:rptSettleOldData:view")
    public String index(ModelMap map) {
       
        //页面条件
        List<CommonVO> months = dateService.commonMonths();
        List<Map<String, String>> reportIds =rptSettleQueryService.listReportInfo();
        map.put("months", months);
        map.put("reportIds", reportIds);
        return "report/rptSettleOldData";
    }

    /**
     * 报表查询
     * @param month
     * @param reportId
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:rptSettleOldData:view")
    public JsonResult list(String month, String reportId ){
    	List<SettleDataVO> list =rptSettleOldDataService.listSettle(month, reportId);

        return JsonResult.success(list);
    }

    
    /**
     * 导出
     */
    @PostMapping("export")
    @ResponseBody
    @RequiresPermissions("report:rptSettleOldData:view")
    public JsonResult export(HttpServletRequest req,
                             HttpServletResponse resp,
                             String temp){
    		
    	try {
    		
    		ObjectMapper om = new ObjectMapper();
    		ParamVO vo = om.readValue(temp, ParamVO.class);
    		for(SettleDataVO s : vo.getLogs()){
    				 
    			byte[] datas = rptSettleOldDataService.export(Long.parseLong(s.getLogId()), s.getIncomeSource());
    			String name = rptSettleOldDataService.getFileName(s.getReportId(),s.getIncomeSource());
    		
    			downloadService.download(req, resp, datas,name);
    		}
    		    		   		
		} catch (Exception e) {
			e.printStackTrace();
			throw new ReportException("导出错误:" + e.getMessage());
		}
    	
        return JsonResult.success();
    }
    
}
