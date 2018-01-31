package com.loushuiyifan.report.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptFundsFeeStatusDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.vo.FundsStatusVO;

/**
 * 
 * @author yuxk
 * @date 2017-11-15
 */
@Service
public class RptFundsFeeStatusService {
	private static final Logger logger = LoggerFactory.getLogger(RptFundsFeeStatusService.class);
	@Autowired
	RptFundsFeeStatusDAO rptFundsFeeStatusDAO;
	
	/**
	 * 查询
	 */
	public List<FundsStatusVO> list(String month, String reportId){
		
		List<FundsStatusVO> list =rptFundsFeeStatusDAO.listFundsFee(month, reportId);
		
		return list;
	}
	
	/**
     * 回退
     */
	public void quit(Long userId,String month, String reportId){
		//TODO 存过还没确定
		SPDataDTO dto = new SPDataDTO();
		dto.setUserId(userId);
		dto.setMonth(month);
		dto.setReportId(reportId);
		rptFundsFeeStatusDAO.quitData(dto);
		int code = dto.getRtnCode();
        if (code != 0) {//非0为失败
            throw new ReportException("1数据回退失败: " + dto.getRtnMsg());
        }
	}
	
	
	/**
     * 下载电子档案附件
     */
	
	
	
	
}
