package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

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

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptTaxQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptTaxQuery")
public class RptTaxQueryController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptTaxQueryController.class);
	
	@Autowired
	RptTaxQueryService rptTaxQueryService;
	
	/**
	 * 税务报表页面
	 * @param map
	 * @param user
	 * @return
	 */
	@GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Map<String,String>> orgs = rptTaxQueryService.listAreaInfo();
        List<CommonVO> months = dateService.aroundMonths(5);
        
        map.put("orgs", orgs);
        map.put("months", months);
        return "report/rptTaxQuery";
    }
	
	
	/**
	 * 查询
	 */
	@PostMapping("list")
	@ResponseBody
	public JsonResult list(String month, String latnId){
		
		
		return JsonResult.success();
	}
	
	
	/**
	 * 导出
	 */
	@PostMapping("export")
	@ResponseBody
	public JsonResult export(String month, String latnId){
		
		
		return JsonResult.success();
	}
	
	
	
}
