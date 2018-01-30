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
import com.loushuiyifan.report.dao.RptImportCutDataDAO;
import com.loushuiyifan.report.exception.ReportException;
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
	@Autowired
    RptImportCutDataDAO rptImportCutDataDAO;
	
	@GetMapping
	@RequiresPermissions("report:rptRuleConfig:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
		Long userId = user.getId();

        //页面条件
        List<Organization> orgs = rptRuleConfigService.listAllByUserForRule(userId);
        List<CommonVO> months = dateService.commonMonths();
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
	
	/**
	 * 修改
	 * @return
	 */
	@PostMapping("update")
    @ResponseBody
    public JsonResult update(String month,String latnId, String cardType,String discount, 
    		                  String platformAmount,String inactiveAmount,Long logId,
    		                  @ModelAttribute("user") User user) {
		 
			Long userId = user.getId();
			double dis =Double.parseDouble(discount);
			if(dis<0 ||dis>1){
				throw new ReportException("超出折扣率的取值范围：[0,1]");
			}
			List<String> num =rptRuleConfigService.checkUsers(logId, userId);
			
			if(num.size()!=1 && userId !=19306&& userId !=119 && userId !=113 &&userId !=34951&& userId !=216630&& userId !=1082230){
				throw new ReportException("该用户不具有修改权限");		
			}
			
			rptRuleConfigService.updateRule(month,cardType, discount, platformAmount, inactiveAmount,logId);

		 	
		 return JsonResult.success();
    }
	
	
	
	
	
	
	
	
	
	
	
}
