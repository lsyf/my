package com.loushuiyifan.da.controller;

import com.loushuiyifan.da.service.DAService;
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
@RequestMapping("da")
public class DAController {

    @Autowired
    DAService daService;

    @GetMapping("{name}")
    public String report(@PathVariable String name) {
        return "da/" + name;
    }


    @PostMapping("da1")
    @ResponseBody
    public List<Map<String,Object>> da1(String month) {
        return daService.listTwoYear();
    }


}
