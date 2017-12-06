package com.loushuiyifan.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptSettleQueryService;

@Controller
@RequestMapping("rptSettleDetail")
public class RptSettleDetailController extends BaseReportController{

	@Autowired
	RptSettleQueryService rptSettleQueryService;
	/**
     * 集团结算报表查询界面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map,Long logId,String incomeSource) {
    	Map<String, Object> data = rptSettleQueryService.listDetail(logId, incomeSource);
    	map.addAllAttributes(data);
        return "report/rptSettleDetail";
    }
 
    
    
}
