package com.loushuiyifan.report.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 财务报表查询
 *
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
@RequestMapping("rptQuery")
public class RptQueryController {


    @GetMapping
    public String index() {
        return "report/rptQuery";
    }


}
