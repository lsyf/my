package com.loushuiyifan.report.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.RptSettleQueryService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.ParamVO;
import com.loushuiyifan.report.vo.SettleDataVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("rptSettleQuery")
public class RptSettleQueryController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptSettleQueryController.class);
	@Autowired
	RptSettleQueryService rptSettleQueryService;
	/**
     * 集团结算报表查询界面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:rptSettleQuery:view")
    public String index(ModelMap map) {
       
        //页面条件
        List<CommonVO> months = dateService.commonMonths();
        List<Map<String, String>> reportIds =rptSettleQueryService.listReportInfo();
        map.put("months", months);
        map.put("reportIds", reportIds);
        return "report/rptSettleQuery";
    }

    /**
     * 报表查询
     * @param month
     * @param reportId
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:rptSettleQuery:view")
    public JsonResult list(String month, String reportId ){
    	List<SettleDataVO> list =rptSettleQueryService.listSettle(month, reportId);

        return JsonResult.success(list);
    }

   /**
    * 详情查看
    */
    @PostMapping("detail")
    @ResponseBody
    @RequiresPermissions("report:rptSettleQuery:view")
    public JsonResult detail(Long logId,String incomeSource){
    	Map<String, Object> map = rptSettleQueryService.listDetail(logId, incomeSource);
        return JsonResult.success(map);
    }
    
    /**
     * 导出
     */
    @PostMapping("export")
    @ResponseBody
    @RequiresPermissions("report:rptSettleQuery:view")
    public JsonResult export(HttpServletRequest req,
                             HttpServletResponse resp,
                             String temp){
    		
    	try {
    		
    		ObjectMapper om = new ObjectMapper();
    		ParamVO vo = om.readValue(temp, ParamVO.class);
    		for(SettleDataVO s : vo.getLogs()){
    				 
    			byte[] datas = rptSettleQueryService.export(Long.parseLong(s.getLogId()), s.getIncomeSource());
    			String name = rptSettleQueryService.getFileName(s.getReportId(),s.getIncomeSource());
    		
    			downloadService.download(req, resp, datas,name);
    		}
    		    		   		
		} catch (Exception e) {
			e.printStackTrace();
			throw new ReportException("导出错误:" + e.getMessage());
		}
    	
        return JsonResult.success();
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
    public JsonResult listAudit(String month,
    		@RequestParam("logs[]") String[] logs) {
       
    	Map<String, Object> map =null;
    	for(int i=0; i<logs.length; i++){
			Long logId = Long.parseLong(logs[0]);
			String reportId =logs[1];
			String incomeSource =logs[2];
    		map = rptSettleQueryService.listAudit(month,reportId,logId,incomeSource);
             
       }
        
        return JsonResult.success(map);
    }

    /**
     * 审核报表
     */
    @PostMapping("audit")
    @ResponseBody
    public JsonResult audit(Long rptCaseId, String status, String comment,String incomeSource,
                            @ModelAttribute("user") User user) {
       //TODO 审核存过返回值改为0：成功，非0：失败
    	//PKG_RPT_SETT.auditRpt
    	Long userId = user.getId();
        rptSettleQueryService.audit(rptCaseId,status, comment, userId);
        return JsonResult.success();
    }
    
    
    
}
