package com.loushuiyifan.report.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptQueryComDetailService2017;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.RptQueryDataVO;
import com.loushuiyifan.system.vo.JsonResult;

/**
 * 通信主页明细报表查询
 *
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
@RequestMapping("rptQueryComDetail2017")
public class RptQueryComDetailController2017 extends BaseReportController {


    @Autowired
    RptQueryComDetailService2017 rptQueryComDetailService;

    @GetMapping
    @RequiresPermissions("report:rptQueryComDetail2017:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listAllByUser(userId, 4);
        List<CommonVO> months = dateService.prepareMonth();

        map.put("orgs", orgs);
        map.put("months", months);
        return "report/rptQueryComDetail2017";
    }

    /**
     * 报表查询
     *
     * @param month
     * @param latnId
     * @param type
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month,
                           String latnId,
                           String type,
                           @ModelAttribute("user") User user) {

        Long userId = user.getId();
        RptQueryDataVO vo = rptQueryComDetailService.list(month, latnId, type, userId);
        return JsonResult.success(vo);
    }

    /**
     * 报表导出
     *
     * @param month
     * @param latnId
     * @param type
     * @return
     */
    @PostMapping("export")
    @ResponseBody
    public void export(HttpServletRequest req,
                       HttpServletResponse resp,
                       String month,
                       String latnId,
                       String type,
                       Boolean isMulti) throws Exception {
        String path = rptQueryComDetailService.export(month,
                latnId,
                type,
                isMulti);

        downloadService.download(req, resp, path);
    }


    @PostMapping("remove")
    @ResponseBody
    public JsonResult remove(String month,
                             String latnId,
                             String type) throws Exception {
        rptQueryComDetailService.remove(month,
                latnId,
                type);
        return JsonResult.success();
    }

}
