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
import com.loushuiyifan.report.dao.RptSettleQueryDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.SettleExportServ;
import com.loushuiyifan.report.vo.RptAuditVO;
import com.loushuiyifan.report.vo.SettleDataVO;

@Service
public class RptSettleQueryService {
    private static final Logger logger = LoggerFactory.getLogger(RptSettleQueryService.class);
    @Autowired
    RptSettleQueryDAO rptSettleQueryDAO;

    /**
     * 查询
     */
    public List<SettleDataVO> listSettle(String month, String reportId) {

        List<SettleDataVO> list = rptSettleQueryDAO.listData(month, reportId);
        
        return list;
    }
    
    /**
     * 详情查询
     * @param logId
     * @param incomeSource
     * @return
     */
    public Map<String, Object> listDetail(Long logId,String incomeSource) {

    	List<Map<String, String>> detail =rptSettleQueryDAO.detail(logId, incomeSource);  
    	
        List<Map<String, String>> titles = rptSettleQueryDAO.titleOld(logId);
        List<Map<String, String>> datas =rptSettleQueryDAO.listOld(logId);
        if (datas == null || datas.isEmpty()) {
			throw new ReportException("原始数据为空!");
		}
        
        Map<String, Object> map = Maps.newHashMap();
        map.put("datas", datas);
        map.put("titles", titles);
        map.put("detail", detail);
        
        return map;
    }

    public Map<String, Object> listAudit(String month,
							            String reportId,
							            Long logId,
							            String incomeSource){
    	
    	Long rptCaseId = logId + Long.parseLong(incomeSource);
    	Map param = new HashMap();
        param.put("rptCaseId", rptCaseId);
        param.put("month",month);
        param.put("reportId",reportId);
        param.put("incomeSource",incomeSource);
        rptSettleQueryDAO.selectAudits(param);
        List<RptAuditVO> list = (List<RptAuditVO>) param.get("list");
        if (list == null || list.size() == 0) {
            throw new ReportException("审核信息为空");
        }
    	Map<String, Object> map = Maps.newHashMap();
    	map.put("list", list);
    	map.put("rptCaseId", rptCaseId);
    	map.put("incomeSource", incomeSource);
    	
    	return map;
    }
    
    public void audit(Long rptCaseId, String incomeSource,String status, String comment, Long userId) {
        SPDataDTO dto = new SPDataDTO();
        dto.setRptCaseId(rptCaseId+Long.parseLong(incomeSource));
        dto.setUserId(userId);
        dto.setStatus(status);
        dto.setComment(comment);
        rptSettleQueryDAO.auditRpt(dto);
        if (dto.getRtnCode() != 0) {//非0审核失败
            throw new ReportException(dto.getRtnMsg());
        }
    }
    
    /**
     * 导出数据
     */
    public byte[] export(Long logId,String incomeSource) throws Exception {
    	String[] key = { "REPORTID", "REPORTNAME", "ACCTMONTH", "AREAID", "AREANAME", "HORCODE", "VERCODE",
    			"INDEXALLIS", "INCOMESOURCE", "SOURCENAME", "INDEXDATA"};
    	String[] value = { "报表编号", "报表名称", "账期", "地市编号", "地市名", "渠道编码", "指标编码", "指标名称", "收入来源编号", "收入来源名称", "数据" };

    	String[] Keys = { "BUKRS", "REPORTID", "EXTEND_001", "EXTEND_002", "EXTEND_003" };
    	String[] Values = { "组织代码", "报表编号", "扩展字段1", "扩展字段2", "扩展字段3" };
    	List<Map<String, String>> row =rptSettleQueryDAO.detail(logId, incomeSource);      	
        
    	List<Map<String, String>> titles = rptSettleQueryDAO.titleOld(logId);
        List<Map<String, String>> datas =rptSettleQueryDAO.listOld(logId);        
        if (datas == null || datas.isEmpty()) {
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
        
		//原始数据表头
    	List<Map<String, String>> col = new ArrayList<Map<String, String>>();    	
		for (int i = 0; i < Keys.length; i++) {
			String k = key[i];
			String v = value[i];
			Map map = new HashMap();
			map.put("id", k);
			map.put("name", v);
			cols.add(map);
		}
		
        byte[] data = new SettleExportServ().column(cols, col).row(datas, row).exportData();

        return data;
    }
    
    public String getFileName(String reportId,String incomeSource){
    	
    	return "集团结算_"+reportId+"_"+incomeSource+"_.xls";
    }
    
    /**
     * 报表编号查询
     */
    public List<Map<String, String>> listReportInfo() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> m = Maps.newHashMap();
        m.put("id", "0");
        m.put("name", "全部");
        m.put("data", "0");
        m.put("lvl", "1");
        list.add(m);

        List<Map<String, String>> list2 = rptSettleQueryDAO.listReportInfo();
        list.addAll(list2);
        return list;
    }


}
