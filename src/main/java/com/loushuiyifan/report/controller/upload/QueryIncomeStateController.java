package com.loushuiyifan.report.controller.upload;

import java.util.List;
import java.util.Map;

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
import com.loushuiyifan.report.service.QueryIncomeStateService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.TransLogVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("queryIncomeState")
public class QueryIncomeStateController extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(QueryIncomeStateController.class);
	
	@Autowired
	QueryIncomeStateService queryIncomeStateService;
	
	/**
     * 省公司集中处理来源完成进度界面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {

        //页面条件       
        List<CommonVO> months = dateService.aroundMonths(5);       
        map.put("months", months);
       
        return "report/upload/queryIncomeState";
    }
    
    
    /**
     * 省公司集中处理来源完成进度-查询
     *
     * @param month
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult listLog(String month, 
    		                       String typeId) {

        List<Map> list = queryIncomeStateService.list(month, typeId);

        return JsonResult.success(list);
    }
    
    /**
     * 更新状态
     */
    @PostMapping("updateLog")
    @ResponseBody
    public JsonResult updateTransLog(){
    	
    	 return JsonResult.success();
    }
    
    
    
    
    
}
