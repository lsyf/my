package com.loushuiyifan.report.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptQueryIncomeDetailDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.vo.IncomeDetailVO;

@Service
public class RptQueryIncomeDetailService {
	private static final Logger logger = LoggerFactory.getLogger(RptQueryIncomeDetailService.class);
		
	
	@Autowired
	RptQueryIncomeDetailDAO rptQueryIncomeDetailDAO;
	
	/**
	 * 查询
	 */
	public List<IncomeDetailVO> list(String startDate,String endDate,String state)
			throws Exception{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	String start =null;
    	String end = null;
    	
    	if(sdf.parse(startDate).getTime() > sdf.parse(endDate).getTime()){
    		logger.info("开始时间大于结束时间");
         	start = endDate;
         	end = startDate;
    	}else if(sdf.parse(startDate).getTime() < sdf.parse(endDate).getTime()){
     		logger.info("#############=="+startDate);
         	logger.info("#############=="+endDate);
     		start = startDate;
     		end = endDate;
    	}else{
     		return null;
     		//month = startDate.substring(0,10).toString();
    	}
    	
    	List<IncomeDetailVO> list =rptQueryIncomeDetailDAO.listIncomeData(start, end, state);
    	 	
		return list;
	}
	
   public List<Map<String,String>> detail(String sessionId){
		
		return rptQueryIncomeDetailDAO.detailById(sessionId);
		
	}
	public List<IncomeDetailVO> findData(String sessionId){
		
		return rptQueryIncomeDetailDAO.findDataById(sessionId);
		
	}
	
	
	public void send(String sessionId) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Map<String,String> map = rptQueryIncomeDetailDAO.selectCodeById(sessionId);
		
		String lstUpd = rptQueryIncomeDetailDAO.selectLstUpd(sessionId);
		long time =sdf.parse(lstUpd).getTime();
		long now = Calendar.getInstance().getTimeInMillis();
		long temp = (now - time) / (1000 * 60);
		if (temp < 15) {
			throw new ReportException("开始采集后, 15分钟内不允许重发");
		}
		
		if("0".equals(map.get("code0")) && "0".equals(map.get("code3"))){
			throw new ReportException("入库成功,不允许重发");
		}else if(!"0".equals(map.get("code3")) && "0".equals(map.get("code2")) ){
			throw new ReportException("文件采集成功, 数据文件有错误");
		}else{
			rptQueryIncomeDetailDAO.updateById(sessionId);
		}
		
		
	}
}
