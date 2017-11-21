package com.loushuiyifan.report.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.loushuiyifan.report.service.RptSettleAmountService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.SettleAmountDataVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptSettleAmount")
public class RptSettleAmountController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptSettleAmountController.class);
	@Autowired
	RptSettleAmountService rptSettleAmountService;
	/**
     * 结算金额查询界面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listAllByUser(userId, 3);
        List<CommonVO> months = dateService.aroundMonths(5);
        
        map.put("orgs", orgs);
        map.put("months", months);
        return "report/rptSettleAmount";
    }

    /**
     * 报表查询
     * @param month
     * @param reportId
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, String latnId, String zbCode ){
    	List<SettleAmountDataVO> list =rptSettleAmountService.listSettle(month, latnId,zbCode);

        return JsonResult.success(list);
    }

   /**
    * 汇总
    */
    @PostMapping("collect")
    @ResponseBody
    public JsonResult collect(String month){
    	//IRPT_ALL_2017.irpt_all_income_sjs
    	rptSettleAmountService.collect(month);
        return JsonResult.success();
    }
    
    /**
     * 下载excel
     */
    @PostMapping("export")
    @ResponseBody
    public JsonResult export(HttpServletRequest req,
				           	HttpServletResponse resp,
				           	String month,
				           	String latnId, 
				           	String zbCode){
    	try {
            byte[] datas = rptSettleAmountService.export(month, latnId, zbCode);
            String name ="2017.xls";

            downloadService.download(req, resp, datas,name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
    	return JsonResult.success();
    }
    
    
    
    
    
}
