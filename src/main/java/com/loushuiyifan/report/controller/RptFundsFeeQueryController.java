package com.loushuiyifan.report.controller;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptFundsFeeQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.FundsFeeVO;
import com.loushuiyifan.system.vo.JsonResult;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("rptFundsFeeQuery")
public class RptFundsFeeQueryController extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(RptFundsFeeQueryController.class);
    @Autowired
    RptFundsFeeQueryService rptFundsFeeQueryService;

    /**
     * 资金缴拨报表查询界面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:rptFundsFeeQuery:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Map<String, String>> orgs = localNetService.listPrctrName(userId);
        List<CommonVO> months = dateService.commonMonths();
        List<Map<String, String>> reportIds = rptFundsFeeQueryService.listReportName();
        map.put("orgs", orgs);
        map.put("months", months);
        map.put("reportIds", reportIds);
        return "report/rptFundsFeeQuery";
    }

    /**
     * 报表查询
     *
     * @param month
     * @param reportId
     * @param prctrName
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult listQuery(String month,
                                String reportId,
                                String prctrName) {
    	
        List<FundsFeeVO> list = rptFundsFeeQueryService.list(month, reportId, prctrName);

        return JsonResult.success(list);
    }

    /**
     * 导出
     */
    @PostMapping("export")
    @ResponseBody
    public JsonResult export(HttpServletRequest req,
                             HttpServletResponse resp,
                             String month,
                             String reportId,
                             String prctrName) {
        //TODO 导出报表名称未定
        try {
            byte[] datas = rptFundsFeeQueryService.export(month, reportId, prctrName);

            String name = getFileName(month, reportId, prctrName);
            downloadService.download(req, resp, datas, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success();
    }

    public String getFileName(String month, String reportId, String prctrName) {
        return month + "_" + reportId + "_" + prctrName + ".xls";
    }
}
