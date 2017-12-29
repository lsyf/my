package com.loushuiyifan.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.RptSettleOldDataDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.SettleExportServ;
import com.loushuiyifan.report.vo.SettleDataVO;

@Service
public class RptSettleOldDataService {
	private static final Logger logger = LoggerFactory.getLogger(RptSettleOldDataService.class);
    
    @Autowired
    RptSettleOldDataDAO rptSettleOldDataDAO;
	 /**
     * 查询
     */
    public List<SettleDataVO> listSettle(String month, String reportId) {

        List<SettleDataVO> list = rptSettleOldDataDAO.listData(month, reportId);
        
        return list;
    }
    
    /**
     * 详情查询
     * @param logId
     * @param incomeSource
     * @return
     */
    public Map<String, Object> listDetail(Long logId,String incomeSource) {

        List<Map<String, String>> titles = rptSettleOldDataDAO.titleOld(logId);
        List<Map<String, String>> datas =rptSettleOldDataDAO.listOld(logId);
        if (datas == null || datas.isEmpty()) {
			throw new ReportException("原始数据为空!");
		}
        
        Map<String, Object> map = Maps.newHashMap();
        map.put("datas", datas);
        map.put("titles", titles);
        
        return map;
    }
    /**
     * 导出数据
     */
    public byte[] export(Long logId,String incomeSource) throws Exception {

    	String[] Keys = { "BUKRS", "REPORT_ID", "EXTEND_001", "EXTEND_002", "EXTEND_003" };
    	String[] Values = { "组织代码", "报表编号", "扩展字段1", "扩展字段2", "扩展字段3" };
    	
    	List<Map<String, String>> titles = rptSettleOldDataDAO.titleOld(logId);
        List<Map<String, String>> rows =rptSettleOldDataDAO.listOld(logId);        
        if (rows == null || rows.isEmpty()) {
			throw new ReportException("原始数据为空!");
		}
        //原始数据表头
    	List<Map<String, String>> cols = new ArrayList<Map<String, String>>();    	
		for (int i = 0; i < Keys.length; i++) {
			String k = Keys[i];
			String v = Values[i];
			Map map = new HashMap();
			map.put("id", k);
			map.put("name", v);
			cols.add(map);
		}		
		cols.addAll(titles);
		logger.info("-------------cols="+cols.size());
		
        byte[] data = new SettleExportServ().column(cols, null).row(rows, null).exportData();

        return data;
    }
    
   public String getFileName(String reportId,String incomeSource){
	   if("0".equals(reportId)){   		
		   reportId="全部";
   	    }
    	return "结算原始数据_"+reportId+"_"+incomeSource+".xls";
    }
}
