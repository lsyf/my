package com.loushuiyifan.data.controller;

import com.loushuiyifan.data.service.DataService;
import com.loushuiyifan.data.vo.DataAnalysis2;
import com.loushuiyifan.data.vo.DataAnalysis3;
import com.loushuiyifan.data.vo.DataAnalysis4;
import com.loushuiyifan.system.vo.JsonResult;
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
    public List<Map<String, Object>> da1(String month) {
        return dataService.da1();
    }


    @PostMapping("da2_listIncomeSource")
    @ResponseBody
    public JsonResult da2_listIncomeSource(String month, String latnId, String latnId2,
                                           String productId, String sourceId) {
        List<DataAnalysis2> list = dataService.da2_listIncomeSource(month, latnId, latnId2, productId, sourceId);
        return JsonResult.success(list);
    }

    @PostMapping("da2_listProduct")
    @ResponseBody
    public JsonResult da2_listProduct(String month, String latnId, String latnId2,
                                      String sourceId, String sourceId2) {
        List<DataAnalysis2> list = dataService.da2_listProduct(month, latnId, latnId2, sourceId, sourceId2);
        return JsonResult.success(list);
    }

    @PostMapping("da2_listLatn")
    @ResponseBody
    public JsonResult da2_listLatn(String month, String latnId, String productId,
                                   String sourceId, String sourceId2) {
        List<DataAnalysis2> list = dataService.da2_listLatn(month, latnId, productId, sourceId, sourceId2);
        return JsonResult.success(list);
    }

    @PostMapping("da3")
    @ResponseBody
    public JsonResult da3(String month, String latnId) {
        List<DataAnalysis3> list = dataService.da3(month, latnId);
        return JsonResult.success(list);
    }

    @PostMapping("da4_listLatn1")
    @ResponseBody
    public JsonResult da4_listLatn1(String month, String discountId) {
        List<DataAnalysis4> list = dataService.da4_listLatn1(month, discountId);
        return JsonResult.success(list);
    }

    @PostMapping("da4_listLatn2")
    @ResponseBody
    public JsonResult da4_listLatn2(String month, String discountId, String billId) {
        List<DataAnalysis4> list = dataService.da4_listLatn2(month, discountId, billId);
        return JsonResult.success(list);
    }

    @PostMapping("da4_listBill")
    @ResponseBody
    public JsonResult da4_listBill(String month, String latnId, String discountId) {
        List<DataAnalysis4> list = dataService.da4_listBill(month, latnId, discountId);
        return JsonResult.success(list);
    }

    @PostMapping("da4_listDiscount1")
    @ResponseBody
    public JsonResult da4_listDiscount1(String month, String latnId) {
        List<DataAnalysis4> list = dataService.da4_listDiscount1(month, latnId);
        return JsonResult.success(list);
    }

    @PostMapping("da4_listDiscount2")
    @ResponseBody
    public JsonResult da4_listDiscount2(String month, String latnId, String billId) {
        List<DataAnalysis4> list = dataService.da4_listDiscount2(month, latnId, billId);
        return JsonResult.success(list);
    }
}
