package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

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
import com.loushuiyifan.report.service.RptQueryCustService2017;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.RptQueryDataVO;
import com.loushuiyifan.system.vo.JsonResult;

/**
 * 财务报表查询
 *
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
@RequestMapping("rptQueryCust2017")
public class RptQueryCustController2017 extends BaseReportController {

    @Autowired
    RptQueryCustService2017 rptQueryCustService;

    @GetMapping
    @RequiresPermissions("report:rptQueryCust2017:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        // 页面条件
        List<Organization> orgs = localNetService.listAllByUser(userId, 4);
        List<CommonVO> months = dateService.prepareMonth();
        List<Map> incomeSources = codeListTaxService.listByType("income_source2017");

        map.put("orgs", orgs);
        map.put("months", months);
        map.put("incomeSources", incomeSources);
        return "report/rptQueryCust2017";
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
    @RequiresPermissions("report:rptQueryCust2017:view")
    public JsonResult list(String month, String latnId, String incomeSource, String type,
                           @ModelAttribute("user") User user) {

        Long userId = user.getId();
        RptQueryDataVO vo = rptQueryCustService.list(month, latnId, incomeSource, type, userId);
        return JsonResult.success(vo);
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
    @RequiresPermissions("report:rptQueryCust2017:view")
    public void export(HttpServletRequest req, HttpServletResponse resp, String month, String latnId,
                       String incomeSource, String type, Boolean isMulti) throws Exception {
        String path = rptQueryCustService.export(month, latnId, incomeSource, type, isMulti);

        downloadService.download(req, resp, path);
    }

    /**
     * 报表审核状态
     *
     * @param month
     * @param latnId
     * @param incomeSource
     * @param type
     * @return
     */
    @PostMapping("listAudit")
    @ResponseBody
    public JsonResult listAudit(String month, String latnId, String incomeSource, String type,
                                @ModelAttribute("user") User user) {
        Long userId = user.getId();
        Map<String, Object> map = rptQueryCustService.listAudit(month, latnId, incomeSource, type, userId);
        return JsonResult.success(map);
    }

    /**
     * 审核报表
     */
    @PostMapping("audit")
    @ResponseBody
    @RequiresPermissions("report:rptQueryCust:audit")
    public JsonResult audit(Long rptCaseId, String status, String comment, @ModelAttribute("user") User user) {

        Long userId = user.getId();
        rptQueryCustService.audit(rptCaseId, status, comment, userId);
        return JsonResult.success();
    }

}
