package com.loushuiyifan.report.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptTaxExportService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptTaxExport")
public class RptTaxExportController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptTaxExportController.class);
	
	@Autowired
	RptTaxExportService rptTaxExportService;
	
	/**
	 * 税号税目的汇总数据页面
	 * @param map
	 * @param user
	 * @return
	 */
	@GetMapping
	@RequiresPermissions("report:rptTaxExport:view")
    public String index(ModelMap map) {

        //页面条件
        List<CommonVO> months = dateService.aroundMonths(5);
        
        map.put("months", months);
        return "report/rptTaxExport";
    }
	
	
	/**
     * 导出
     */
    @PostMapping("export")
    @ResponseBody
    @RequiresPermissions("report:rptTaxExport:view")
    public JsonResult export(HttpServletRequest req,
                           	HttpServletResponse resp,
                           	String month) {
        try {
            byte[] datas = rptTaxExportService.export(month);
            String name =rptTaxExportService.getFileName(month);

            downloadService.download(req, resp, datas,name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success();
    }
	
	
	
}
