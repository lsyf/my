package com.loushuiyifan.report.controller;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.IncomeStatisticsService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 一键汇总
 *
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
@RequestMapping("incomeStatistics")
public class IncomeStatisticsController extends BaseReportController {

    @Autowired
    IncomeStatisticsService incomeStatisticsService;

    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listForC4(userId);
        List<CommonVO> months = dateService.aroundMonths(5);

        map.put("orgs", orgs);
        map.put("months", months);
        return "report/incomeStatistics";
    }


    @PostMapping("exec")
    @ResponseBody
    public JsonResult exec(String month, String latnId, @ModelAttribute("user") User user) {
        Long userId = user.getId();
        incomeStatisticsService.allSum(month, latnId, userId);
        return JsonResult.success();
    }

}
