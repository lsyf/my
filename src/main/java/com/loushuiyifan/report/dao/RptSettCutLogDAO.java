package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptSettCutLog;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.SettCutDataVO;

public interface RptSettCutLogDAO extends MyMapper<RptSettCutLog>{
	Long nextvalKey();
	void deleteLog(Long logId);
	
	List<SettCutDataVO> SettCutLog(String month);
	/**
	 * 校验导入数据
	 * PKG_RPT_SETT.CHECK_SETT_CUT
	 */
	void checkSettleData(SPDataDTO dto);
	/**
	 * 删除存过
	 * PKG_RPT_SETT.IRPT_DEL_SETT_CUT
	 * @param dto
	 */
	void delSettleData(SPDataDTO dto);
	
}
