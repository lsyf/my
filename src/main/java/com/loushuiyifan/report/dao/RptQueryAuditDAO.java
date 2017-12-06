package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.report.dto.SPDataDTO;

public interface RptQueryAuditDAO {

	List<Map<String,String>> listStateForMap(@Param("month") String month,
			                                 @Param("latnId")String latnId);
	
	List<Map<String,String>> listFeeMap(@Param("month") String month,
                                        @Param("latnId")String latnId);
	
	/**
	 * 回退-校验用户权限
	 * PKG_RPT_AUDIT.hasAuditPost2
	 */
	void checkUser(SPDataDTO dto);
	
	/**
	 * 回退存过
	 * del_audit_status
	 */
	void delAuditStatus(SPDataDTO dto);
	
	
	/**
	 * 四审
	 * PKG_RPT_AUDIT.auditRpt_part4
	 */
	void auditRptPart4(SPDataDTO dto);
}
