package com.loushuiyifan.report.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptFundsFeeAuditDAO;
import com.loushuiyifan.report.vo.FundsAuditVO;

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
	
	
	/**
	 * 报表审核
	 */
	public void  auditReport(String month, String reportId)throws Exception{
		
		String c_rpt_sett_audit_id =(reportId+ month).toString();
		String c_acct_month =month;
		String c_report_id =reportId;
		
		
	}
	
	
}
