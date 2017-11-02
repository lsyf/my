package com.loushuiyifan.report.controller;

import com.loushuiyifan.system.vo.JsonResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 一键汇总
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
@RequestMapping("incomeStatistics")
public class IncomeStatisticsController {


    @GetMapping
    public String index() {
        return "report/incomeStatistics";
    }


    @PostMapping("exec")
    @ResponseBody
    public JsonResult exec() {

        return JsonResult.success();
    }

}
