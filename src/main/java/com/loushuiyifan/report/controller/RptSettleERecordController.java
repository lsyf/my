package com.loushuiyifan.report.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptSettleERecordService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.SettleDataVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptSettleERecord")
public class RptSettleERecordController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptSettleERecordController.class);
	@Autowired
	RptSettleERecordService rptrptSettleERecordService;
	/**
     * 集团结算审批流
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:rptSettleERecord:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {

        //页面条件
        List<CommonVO> months = dateService.commonMonths();
        map.put("months", months);
        return "report/rptSettleERecord";
    }

    /**
     * 报表查询
     * @param month
     * @param reportId
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month){
    	List<SettleDataVO> list =rptrptSettleERecordService.listSettle(month);

        return JsonResult.success(list);
    }

   
}
