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
import com.loushuiyifan.report.service.RptQuerySingleBillService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.SingleBillVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptQuerySingleBill")
public class RptQuerySingleBillController extends BaseReportController{

	
	@Autowired
	RptQuerySingleBillService rptQuerySingleBillService;
	
	@GetMapping
    @RequiresPermissions("report:rptQuerySingleBill:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listForC4(userId);
        List<CommonVO> months = dateService.lastMonths(2);

        map.put("orgs", orgs);
        map.put("months", months);
        return "report/rptQuerySingleBill";
    }
	
	
	 /**
     * 报表查询
     */
    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:rptQuerySingleBill:view")
    public JsonResult list(String month,
                           String latnId,
                           String phone) {

    	Map<String,Object> list =rptQuerySingleBillService.list(month, latnId, phone);
        return JsonResult.success(list);
    }

}
