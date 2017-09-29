package com.loushuiyifan.data.controller;

import com.loushuiyifan.data.service.DataService;
import com.loushuiyifan.data.vo.DataAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
@Controller
@RequestMapping("data")
public class DataController {

    @Autowired
    DataService dataService;

    @GetMapping("{name}")
    public String report(@PathVariable String name) {
        return "data/" + name;
    }


    @PostMapping("da1")
    @ResponseBody
    public List<Map<String,Object>> da1(String month) {
        return dataService.da1();
    }


    @PostMapping("da2")
    @ResponseBody
    public List<DataAnalysis> da2(String month) {
        return dataService.da2(month);
    }

}
