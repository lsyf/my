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
import com.loushuiyifan.report.service.RptRuleConfigService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.RuleConfigVO;
import com.loushuiyifan.system.vo.JsonResult;
/**
 * 智能网折扣配置
 *
 * @author yuxk
 * @date 2017/12/2
 */
@Controller
@RequestMapping("rptRuleConfig")
public class RptRuleConfigController extends BaseReportController{

	@Autowired
	RptRuleConfigService rptRuleConfigService;
	
	@GetMapping
	@RequiresPermissions("report:rptRuleConfig:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
		Long userId = user.getId();

        //页面条件
        //List<Organization> orgs2 = localNetService.listAllByUser(userId, 3);
		List<Map<String,String>> orgs = rptRuleConfigService.getCodeName();
		
        List<CommonVO> months = dateService.lastMonths(2);
        List<Map<String,String>> cards =rptRuleConfigService.listCard(); 
        
        map.put("orgs", orgs);
        map.put("months", months);
        map.put("cards", cards);
		
        return "report/rptRuleConfig";
    }
	
	/**
	 * 查询
	 * @param month
	 * @param latnId
	 * @return
	 */
	@PostMapping("list")
    @ResponseBody
    public JsonResult queryList(String month,String latnId, String cardType,
    		                    String discount, Long logId) {
				
		List<RuleConfigVO> list = rptRuleConfigService.list(month, latnId, cardType,discount, logId);
		
        return JsonResult.success(list);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
}
