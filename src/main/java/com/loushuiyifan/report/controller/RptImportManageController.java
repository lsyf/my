package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.loushuiyifan.report.service.RptImportManageService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptImportManage")
public class RptImportManageController extends BaseReportController{

	@Autowired
	RptImportManageService rptImportManageService;
	
	@GetMapping
	@RequiresPermissions("report:rptImportManage:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
		
        return "report/rptImportManage";
    }
	
	
	@PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:rptImportManage:view")
    public JsonResult queryList(String startDate,
    		                    String endDate,
    		                    String fileName,
    		                    String userName,
    		                    @ModelAttribute("user") User user) {
		Long userId = user.getId();
		
		List<Map<String,String>> list =null;
		try {
			
			list = rptImportManageService.list(startDate, endDate, fileName,userName,userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return JsonResult.success(list);
    }
	
}
