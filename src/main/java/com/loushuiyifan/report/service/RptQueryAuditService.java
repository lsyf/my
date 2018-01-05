package com.loushuiyifan.report.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.loushuiyifan.report.dao.RptQueryAuditDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;

@Service
public class RptQueryAuditService {
	
	@Autowired
	RptQueryAuditDAO rptQueryAuditDAO;
	/**
	 * 查询
	 * @return
	 */
	public List<Map<String,String>> list(String month,String latnId){
		
		
		List<Map<String,String>> list = rptQueryAuditDAO.listStateForMap(month, latnId);
		if (list == null ||list.size()==0) {
            throw new ReportException("数据还未准备好！");
        }
		return list;
	}
	
	public List<Map<String,String>> listFee(String month,String latnId){
		
		
		List<Map<String,String>> list = rptQueryAuditDAO.listFeeMap(month, latnId);
		
		return list;
	}
	
	public void  quit(String month,String latnId ,String incomeSource,Long userId)
	          {
		
			SPDataDTO dto = new SPDataDTO();
			dto.setLogId(Long.parseLong("7189")); //logId 代表四级审核的c_post_id
			dto.setUserId(userId);
			rptQueryAuditDAO.checkUser(dto);
			
			String code = dto.getRtnMsg().toString();
	        if (!"Y".equals(code)) {
	            throw new ReportException("回退失败");
	        }				
	
		       
        //执行回退存过
        SPDataDTO dto2 = new SPDataDTO();
        dto2.setMonth(month);
        dto2.setReportId(incomeSource);
        dto2.setLatnId(latnId);
        rptQueryAuditDAO.delAuditStatus(dto2);
        int code2 = dto2.getRtnCode();
        if (code2 != 0) {//非0为失败
            throw new ReportException("数据回退失败: " + dto2.getRtnMsg());
        }
	}
	
	public void audit(String month,Long userId,String codeName) throws Exception{
		
		SPDataDTO dto = new SPDataDTO();
		dto.setUserId(userId);
		dto.setMonth(month);
		dto.setReportId(codeName);
		rptQueryAuditDAO.auditRptPart4(dto);
		
		 int code = dto.getRtnCode();
	     if (code != 0) {//非0为失败
	    	 throw new ReportException("数据四审失败: " + dto.getRtnMsg());
	     }
		
	}
}
