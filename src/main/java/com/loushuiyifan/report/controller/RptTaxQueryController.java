package com.loushuiyifan.report.controller;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.RptTaxQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.RptQueryDataVO;
import com.loushuiyifan.system.vo.JsonResult;
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
@RequestMapping("rptTaxQuery")
public class RptTaxQueryController extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(RptTaxQueryController.class);

    @Autowired
    RptTaxQueryService rptTaxQueryService;

    /**
     * 税务报表页面
     *
     * @param map
     * @param user
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Map<String, String>> orgs = rptTaxQueryService.listAreaInfo();
        List<CommonVO> months = dateService.aroundMonths(5);

        map.put("orgs", orgs);
        map.put("months", months);
        return "report/rptTaxQuery";
    }


    /**
     * 查询
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, String latnId, String taxType) {

        RptQueryDataVO vo = rptTaxQueryService.list(month, latnId, taxType);

        return JsonResult.success(vo);
    }


    /**
     * 导出
     */
    @PostMapping("export")
    @ResponseBody
    public JsonResult export(HttpServletRequest req,
                             HttpServletResponse resp,
                             String month,
                             String latnId,
                             String taxType) {
        try {
            byte[] datas = rptTaxQueryService.export(month, latnId, taxType);
            String name = rptTaxQueryService.getFileName(month, latnId, taxType);

            downloadService.download(req, resp, datas, name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException("导出错误:" + e.getMessage());
        }
        return JsonResult.success();
    }


}
