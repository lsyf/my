package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

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
import com.loushuiyifan.report.service.RptQueryIncomeNumService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.IncomeDetailVO;
import com.loushuiyifan.report.vo.RptIncomeNumVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptQueryIncomeNum")
public class RptQueryIncomeNumController extends BaseReportController{

	@Autowired
	RptQueryIncomeNumService rptQueryIncomeNumService;
	/**
     * 营收稽核系统报账回执汇总页面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
    	List<CommonVO> months = dateService.aroundMonths(5);
    	 map.put("months", months);
        return "report/rptQueryIncomeNum";
    }
    
    
    /**
     * 查询
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, String year,String type){
    	String months ="";  
    	if("0".equals(type)){
    		months =year;
    	}else{
    		months =month;
    	}
    	List<RptIncomeNumVO> list = rptQueryIncomeNumService.list(months,type);
        return JsonResult.success(list);
    }
}
