package com.loushuiyifan.report.controller;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.service.RptQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 财务报表查询
 *
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
@RequestMapping("rptQuery")
public class RptQueryController extends BaseReportController {


    @Autowired
    RptQueryService rptQueryService;

    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listAllByUser(userId, 3);
        List<CommonVO> months = dateService.aroundMonths(5);
        List<Map> incomeSources = codeListTaxService.listByType("income_source2017");

        map.put("orgs", orgs);
        map.put("months", months);
        map.put("incomeSources", incomeSources);
        return "report/rptQuery";
    }

    /**
     * 报表查询
     *
     * @param month
     * @param latnId
     * @param incomeSource
     * @param type
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, String latnId, String incomeSource, String type) {
        Map<String, Object> map = rptQueryService.list(month, latnId, incomeSource, type);
        return JsonResult.success(map);
    }

    /**
     * 报表导出
     *
     * @param month
     * @param latnId
     * @param incomeSource
     * @param type
     * @return
     */
    @PostMapping("export")
    @ResponseBody
    public void export(HttpServletRequest req,
                       HttpServletResponse resp,
                       String month,
                       String latnId,
                       String incomeSource,
                       String type,
                       Boolean isMulti) throws Exception {
        String path = rptQueryService.export(month,
                latnId,
                incomeSource,
                type,
                isMulti);
        Path file = Paths.get(path);
        String name = FilenameUtils.getName(path);
        downloadService.download(req, resp, file, name);
    }


}
