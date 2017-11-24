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
import com.loushuiyifan.report.service.RptQueryIncomeDetailService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.SettleDataVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptQueryIncomeDetail")
public class RptQueryIncomeDetailController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptQueryIncomeDetailController.class);
	
	@Autowired
	RptQueryIncomeDetailService rptQueryIncomeDetailService;
	

	/**
     * 营收稽核系统报账回执详情
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<CommonVO> months = dateService.aroundMonths(5);
        
        map.put("months", months);
       
        return "report/rptQueryIncomeDetail";
    }


    /**
     * 查询
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, String reportId ){
    	
        return JsonResult.success();
    }



}
