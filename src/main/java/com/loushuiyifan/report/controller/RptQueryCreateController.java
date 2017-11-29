package com.loushuiyifan.report.controller;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.bean.RptExcelWyf;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptQueryCreateService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务报表查询
 *
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
@RequestMapping("rptQueryCreate")
public class RptQueryCreateController extends BaseReportController {


    @Autowired
    RptQueryCreateService rptQueryCreateService;

    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listAllCity();
        List<CommonVO> months = dateService.aroundMonths(5);

        map.put("orgs", orgs);
        map.put("months", months);
        return "report/rptQueryCreate";
    }

    /**
     * 报表查询
     *
     * @param month
     * @param latnId
     * @param incomeSource
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month,
                           String latnId,
                           String incomeSource) {
        List<RptExcelWyf> list = rptQueryCreateService.list(month, latnId, incomeSource);
        return JsonResult.success(list);
    }

    @PostMapping("create")
    @ResponseBody
    public JsonResult create(String month,
                           String latnId,
                           String incomeSource) {
        rptQueryCreateService.create(month, latnId, incomeSource);
        return JsonResult.success();
    }


    @PostMapping("remove")
    @ResponseBody
    public JsonResult remove(@RequestParam("excelIds[]") Long[] excelIds) {
        rptQueryCreateService.remove(excelIds);
        return JsonResult.success();
    }

}
