package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.report.vo.SettleDataVO;

public interface RptSettleERecordDAO {
	List<SettleDataVO> listData(String month);
}
