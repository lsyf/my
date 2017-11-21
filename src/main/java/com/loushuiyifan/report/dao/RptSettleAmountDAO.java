package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.SettleAmountDataVO;

public interface RptSettleAmountDAO {
	List<SettleAmountDataVO> listData(@Param("month") String month,
									  @Param("latnId") String latnId,
			                          @Param("zbCode") String zbCode);
	
	List<Map<String, String>> listDataForMap(@Param("month") String month,
			                                 @Param("latnId") String latnId,
                                             @Param("zbCode") String zbCode);
	
	String[] listCodeName();
	
	/**
	 * 汇总存过
	 * IRPT_ALL_2017.irpt_all_income_sjs
	 * @param month
	 */
	void collectData(SPDataDTO dto);
}
