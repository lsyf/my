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

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptSettleQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.SettleDataVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptSettleQuery")
public class RptSettleQueryController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptSettleQueryController.class);
	@Autowired
	RptSettleQueryService rptSettleQueryService;
	/**
     * 集团结算报表查询界面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<CommonVO> months = dateService.aroundMonths(5);
        List<Map<String, String>> reportIds =rptSettleQueryService.listReportInfo();
        map.put("months", months);
        map.put("reportIds", reportIds);
        return "report/rptSettleQuery";
    }

    /**
     * 报表查询
     * @param month
     * @param reportId
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, String reportId ){
    	List<SettleDataVO> list =rptSettleQueryService.listSettle(month, reportId);

        return JsonResult.success(list);
    }

   /**
    * 详情查看
    */
    @PostMapping("detail")
    @ResponseBody
    public JsonResult detail(Long logId,String incomeSource){
    	Map<String, Object> map = rptSettleQueryService.listDetail(logId, incomeSource);
        return JsonResult.success(map);
    }
    
    /**
     * 导出
     */
    @PostMapping("export")
    @ResponseBody
    public JsonResult export(String month, String reportId ){
    	
        return JsonResult.success();
    }
    
    
    /**
     * 审核
     */
    //TODO
    
    
    
}
