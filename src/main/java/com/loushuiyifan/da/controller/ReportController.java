package com.loushuiyifan.da.controller;


import com.loushuiyifan.da.service.ReportService;
import com.loushuiyifan.da.vo.Report1;
import com.loushuiyifan.da.vo.Report2;
import com.loushuiyifan.da.vo.Report3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 漏水亦凡
 * @email 979365556@qq.com
 */
@Controller
@RequestMapping("da0")
public class ReportController {

    @Autowired
    ReportService reportService;

    @GetMapping("{name}")
    public String report(@PathVariable String name) {
        return "da/" + name;
    }


    /**
     * 全省移动固网预算堆积图
     *
     * @param start
     * @param end
     * @return
     */
    @PostMapping("provinceMFBg")
    @ResponseBody
    public List<Report1> provinceMFBg(String start, String end) {
        List<Report1> list = reportService.getProvinceMFBg(start, end);
        return list;
    }

    /**
     * 分产品层级饼图
     *
     * @return
     */
    @PostMapping("productLevel")
    @ResponseBody
    public Report2 productLevel(String month) {
        Report2 report2 = reportService.getProductLevel(month);
        return report2;
    }

    @PostMapping("report3")
    @ResponseBody
    public List<Report3> report3(String month) {
        List<Report3> list = reportService.getReport3(month);
        return list;

    }

}
