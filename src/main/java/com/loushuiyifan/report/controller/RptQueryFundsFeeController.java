package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.loushuiyifan.report.service.RptQueryFundsFeeService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.FundsFeeVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptQueryFundsFee")
public class RptQueryFundsFeeController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptQueryFundsFeeController.class);
	@Autowired
	RptQueryFundsFeeService RptQueryFundsFeeService;
	/**
     * 资金缴拨报表查询界面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Map<String, String>> orgs = localNetService.listPrctrName(userId);
        List<CommonVO> months = dateService.aroundMonths(5);
        List<Map<String, String>> reportIds =localNetService.listReportName();
        map.put("orgs", orgs);
        map.put("months", months);
        map.put("reportIds", reportIds);
        return "report/rptQueryFundsFee";
    }

    /**
     * 报表查询
     * @param month
     * @param reportId
     * @param prctrName
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult listQuery(String month, 
	    		                String reportId,
	    		                String prctrName
	    		                ){
    	List<FundsFeeVO> list =RptQueryFundsFeeService.list(month, reportId, prctrName);
       

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
            byte[] datas = RptQueryFundsFeeService.export(month, reportId, prctrName);
            String name ="201710.xls";

            downloadService.download(req, resp, datas,name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success();
    }
}
