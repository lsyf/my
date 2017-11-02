package com.loushuiyifan.report.controller.rest;

import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.system.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 本地网（地市）控制
 *
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@RestController
@RequestMapping("codeListTax")
public class CodeListTaxController {

    @Autowired
    CodeListTaxService codeListTaxService;


    /**
     * 根据用户 获取本地网列表
     */
    @PostMapping("listByType")
    public JsonResult listByType(String type) {
        List<Map> list = codeListTaxService.listByType(type);
        return JsonResult.success(list);
    }


}
