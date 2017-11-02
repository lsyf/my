package com.loushuiyifan.report.controller.rest;

import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报表日期控制
 *
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@RestController
@RequestMapping("date")
public class DateController {

    @Autowired
    DateService dateService;

    /**
     * 周围几个月(例如本月+前两月+后两月)
     *
     * @param num
     * @return
     */
    @PostMapping("aroundMonths")
    public JsonResult aroundMonths(Integer num) {
        List<CommonVO> list = dateService.aroundMonths(num);
        return JsonResult.success(list);
    }

    /**
     * 前几个月(例如本月+前两月)
     * 使用中: 1、收入导入界面
     * @param num
     * @return
     */
    @PostMapping("lastMonths")
    public JsonResult lastMonths(Integer num) {
        List<CommonVO> list = dateService.lastMonths(num);
        return JsonResult.success(list);
    }

}
