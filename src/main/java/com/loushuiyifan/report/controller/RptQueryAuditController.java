package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
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
import com.loushuiyifan.report.service.RptQueryAuditService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptQueryAudit")
public class RptQueryAuditController extends BaseReportController{
		
	@Autowired
	RptQueryAuditService rptQueryAuditService;
	
	/**
	 * 收入来源审核进度
	 * 四审页面
	 */
	@GetMapping
	@RequiresPermissions("report:rptQueryAudit:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
		Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listAllByUser(userId, 3);
        List<CommonVO> months = dateService.commonMonths();
        List<Map<String, String>> incomeSources = codeListTaxService.listIncomeSource(2, "income_source2017");
        map.put("orgs", orgs);
        map.put("months", months);
        map.put("incomeSources", incomeSources);
		
        return "report/rptQueryAudit";
    }
	
	/**
	 * 查询状态
	 * @param month
	 * @param latnId
	 * @return
	 */
	@PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:rptQueryAudit:view")
    public JsonResult queryList(String month,String latnId) {
				
		List<Map<String,String>> list = rptQueryAuditService.list(month, latnId);
		
        return JsonResult.success(list);
    }
	
	/**
	 * 查询金额
	 * @param month
	 * @param latnId
	 * @return
	 */
	@RequiresPermissions("report:rptQueryAudit:view")
	@PostMapping("listFee")
    @ResponseBody
    public JsonResult list(String month,String latnId) {
				
		List<Map<String,String>> list = rptQueryAuditService.listFee(month, latnId);
		
        return JsonResult.success(list);
    }
	
	
	
	/**
	 * 回退
	 * @param month
	 * @param latnId
	 * @param user
	 * @return
	 */
	@PostMapping("quit")
    @ResponseBody
    @RequiresPermissions("report:rptQueryAudit:quit")
    public JsonResult quit(String month,String latnId,String incomeSource,    		                    
    		                    @ModelAttribute("user") User user) {
		Long userId = user.getId();
		
		try {
			
			rptQueryAuditService.quit(month, latnId, incomeSource, userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return JsonResult.success();
    }
	
	/**
     * 审核
     */
	@PostMapping("audit")
    @ResponseBody
    @RequiresPermissions("report:rptQueryAudit:audit")
    public JsonResult audit(String month, 
    		                @RequestParam("logs[]") String[] logs,  		                    
    		                @ModelAttribute("user") User user) {
		Long userId = user.getId();
		// 判断用户是否有相应审核权限
		Subject subject = SecurityUtils.getSubject();
		subject.checkPermission("report:rptQueryCust:audit");
		try {
			for(int i=0; i< logs.length; i++){
				String codeName = logs[i];
				rptQueryAuditService.audit(month, userId, codeName);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return JsonResult.success();
    }
}
