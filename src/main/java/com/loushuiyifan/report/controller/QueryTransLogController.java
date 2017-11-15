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
import com.loushuiyifan.report.service.QueryTransLogService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.TransLogVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("queryTransLog")
public class QueryTransLogController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(QueryTransLogController.class);
	
	@Autowired
	QueryTransLogService queryTransLogService;
	
	/**
     * 收入来源传输日志界面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listAllByUser(userId, 3);
        List<CommonVO> months = dateService.aroundMonths(5);
        List<Map<String, String>> incomeSources = codeListTaxService.listTaxSource(2,"income_source2017");
        map.put("orgs", orgs);
        map.put("months", months);
        map.put("incomeSources", incomeSources);
        return "report/queryTransLog";
    }
    
    
    /**
     * 收入来源传输日志-查询
     *
     * @param month
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult listTransLog(String month, 
		    		               String latnId,
		    		               String incomeSource,
		    		               String taxtId
		    		               ){

        List<TransLogVO> list = queryTransLogService.list(month, 
        		latnId,incomeSource,taxtId);

        return JsonResult.success(list);
    }
    
    /**
     * 导出
     */
    @PostMapping("downLoad")
    @ResponseBody
    public JsonResult downLoadTransLog (HttpServletRequest req,
		                           HttpServletResponse resp,
		                           String month, 
		    		               String latnId,
		    		               String incomeSource,
		    		               String taxtId
		                           ){
    	//TODO
    	try {
    		queryTransLogService.downExcel(month, latnId, incomeSource, Integer.parseInt(taxtId));

		} catch (Exception e) {
			e.printStackTrace();
		}
    	return JsonResult.success();   	 
    }
    
    /**
     * 电子档案下载
     */
    @PostMapping("downLog")
    @ResponseBody
    public void downLoadTran(HttpServletRequest req,
				    		 HttpServletResponse resp,
				             String month,    		
				    		 String batchId){
    	try {
    		String path =queryTransLogService.downLoadFile(batchId, month);
        	
        	downloadService.download(req, resp, path);
    		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
    	
    }
    
    
    
    
}
