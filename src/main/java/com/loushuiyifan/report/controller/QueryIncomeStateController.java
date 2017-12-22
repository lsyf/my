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

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.QueryIncomeStateService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.TransLogVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("queryIncomeState")
public class QueryIncomeStateController extends BaseReportController {
	private static final Logger logger = LoggerFactory.getLogger(QueryIncomeStateController.class);

	@Autowired
	QueryIncomeStateService queryIncomeStateService;

	/**
	 * 省公司集中处理来源完成进度界面
	 *
	 * @return
	 */
	@GetMapping
	@RequiresPermissions("report:queryIncomeState:view")
	public String index(ModelMap map, @ModelAttribute("user") User user) {

		// 页面条件
		List<CommonVO> months = dateService.commonMonths();
		map.put("months", months);

		return "report/queryIncomeState";
	}

	/**
	 * 省公司集中处理来源完成进度-查询
	 *
	 * @param month
	 * @return
	 */
	@PostMapping("list")
	@ResponseBody
	@RequiresPermissions("report:queryIncomeState:view")
	public JsonResult list(String month, String status) {
		List<TransLogVO> list = null;

		list = queryIncomeStateService.list(month, status);

		return JsonResult.success(list);
	}

	/**
	 * 更新状态
	 */
	@PostMapping("changeState")
	@ResponseBody
	@RequiresPermissions("report:queryIncomeState:change")
	public JsonResult update(@RequestParam("logs[]") String[] logs, String status, @ModelAttribute("user") User user) {
		
		// 校验时间
		 dateService.checkIncomeSourceProcess();
		
		 if("全部".equals(status)){
			 throw new ReportException("您无权做此操作！"); 
		 }
		 try {
			 for(String subId : logs){
				 queryIncomeStateService.changeState(status, Long.parseLong(subId));
						 
			 }
		} catch (Exception e) {
			e.getStackTrace();
		}
		 
		 
		return JsonResult.success();
	}

}
