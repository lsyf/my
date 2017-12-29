package com.loushuiyifan.report.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.RptFundsFeeAuditDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.vo.FundsAuditVO;
import com.loushuiyifan.report.vo.RptAuditVO;

/**
 * 
 * @author yuxk
 * @date 2017-11-15
 */
@Service
public class RptFundsFeeAuditService {
	private static final Logger logger = LoggerFactory.getLogger(RptFundsFeeAuditService.class);
	@Autowired
	RptFundsFeeAuditDAO rptFundsFeeAuditDAO;
	
	/**
	 * 查询
	 */
	public List<FundsAuditVO> list(String month, String reportId){
		
		List<FundsAuditVO> list =rptFundsFeeAuditDAO.listFundsFee(month, reportId);
	
		return list;
	}
	
	public Map<String, Object> listAudit(String month, String reportId){
			StringBuilder sb = new StringBuilder(); 
			sb.append(reportId).append(month);
			Long rptCaseId =Long.parseLong(sb.toString());
			Map param = new HashMap();
			param.put("rptCaseId", rptCaseId);
			param.put("month",month);
			param.put("reportId",reportId);
			
			//PKG_RPT_BALANCE.getAuditInfo
			rptFundsFeeAuditDAO.selectAudits(param);
			List<RptAuditVO> list = (List<RptAuditVO>) param.get("list");
			if (list == null || list.size() == 0) {
			throw new ReportException("审核信息为空");
			}
			Map<String, Object> map = Maps.newHashMap();
			map.put("list", list);
			map.put("rptCaseId", rptCaseId);
			
			return map;
		}
	
	/**
	 * 报表审核
	 */
	public void audit(Long rptCaseId,String status, String comment, Long userId) {
        if("".equals(comment)){
        	throw new ReportException("审核意见不可为空！");
        }
		
		SPDataDTO dto = new SPDataDTO();
        dto.setRptCaseId(rptCaseId);
        dto.setUserId(userId);
        dto.setStatus(status);
        dto.setComment(comment);
      //PKG_RPT_BALANCE.auditRpt
        rptFundsFeeAuditDAO.auditRpt(dto);
        Integer code = dto.getRtnCode();
        if (code != 0) {//非0审核失败
            throw new ReportException(dto.getRtnMsg());
        }
    }
    	
	
}
