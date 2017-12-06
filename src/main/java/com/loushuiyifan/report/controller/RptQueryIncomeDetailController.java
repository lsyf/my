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
import com.loushuiyifan.report.service.RptQueryIncomeDetailService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.IncomeDetailVO;
import com.loushuiyifan.report.vo.SettleDataVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptQueryIncomeDetail")
public class RptQueryIncomeDetailController {
	private static final Logger logger = LoggerFactory.getLogger(RptQueryIncomeDetailController.class);
	
	@Autowired
	RptQueryIncomeDetailService rptQueryIncomeDetailService;
	

	/**
     * 营收稽核系统报账回执详情页面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:rptQueryIncomeDetail:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {

        return "report/rptQueryIncomeDetail";
    }


    /**
     * 查询
     */
    @RequiresPermissions("report:rptQueryIncomeDetail:view")
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String startDate,String endDate,String state){
    	List<IncomeDetailVO> list =null;
    	try {
    		
    		list = rptQueryIncomeDetailService.list(startDate, endDate, state);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return JsonResult.success(list);
    }

    /**
     * 根据sessionId查询
     */
    @PostMapping("find")
    @ResponseBody
    @RequiresPermissions("report:rptQueryIncomeDetail:view")
    public JsonResult selectList(String sessionId){
    	List<IncomeDetailVO> list = rptQueryIncomeDetailService.findData(sessionId);
        return JsonResult.success(list);
    }
    
    /**
     * 详情查询
     */
    @PostMapping("detail")
    @ResponseBody
    @RequiresPermissions("report:rptQueryIncomeDetail:view")
    public JsonResult detailList(String sessionId){
    	//TODO 待写
    	List<Map<String,String>> list = rptQueryIncomeDetailService.detail(sessionId);
        return JsonResult.success(list);
    }
    
    /**
     * 重发
     */
    @PostMapping("repeat")
    @ResponseBody
    @RequiresPermissions("report:rptQueryIncomeDetail:view")
    public JsonResult repeat(@RequestParam("logs[]") String[] logs){
    	try {
    		for(String sessionId : logs){
        		rptQueryIncomeDetailService.send(sessionId);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        return JsonResult.success();
    }
}
