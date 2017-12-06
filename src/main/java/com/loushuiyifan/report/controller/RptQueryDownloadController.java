package com.loushuiyifan.report.controller;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.bean.RptExcelWyf;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptQueryCreateService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 一键下载
 *
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
@RequestMapping("rptQueryDownload")
public class RptQueryDownloadController extends BaseReportController {


    @Autowired
    RptQueryCreateService rptQueryCreateService;

    @GetMapping
    @RequiresPermissions("report:rptQueryDownload:view")
    public String index(ModelMap map) {

        //页面条件
        List<CommonVO> months = dateService.aroundMonths(5);

        map.put("months", months);
        return "report/rptQueryDownload";
    }

    /**
     * 报表查询
     *
     * @param month
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, @ModelAttribute("user") User user) {
        Long userId = user.getId();
        List<RptExcelWyf> list = rptQueryCreateService.listByUser(month, userId);
        return JsonResult.success(list);
    }


    @PostMapping("downloadZip")
    @ResponseBody
    public void downloadZip(HttpServletRequest req,
                            HttpServletResponse resp,
                            @RequestParam("excelIds[]") Long[] excelIds) throws Exception {
        String path = rptQueryCreateService.downloadZip(excelIds);
        downloadService.download(req, resp, path);
    }

    @PostMapping("downloadReport")
    @ResponseBody
    public void downloadReport(HttpServletRequest req,
                         HttpServletResponse resp,
                         Long excelId) throws Exception {
        String path = rptQueryCreateService.getFilePath(excelId);
        downloadService.download(req, resp, path);
    }

}
