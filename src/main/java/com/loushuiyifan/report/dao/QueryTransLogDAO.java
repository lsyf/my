package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.StatToGroup;
import com.loushuiyifan.report.vo.TransLogVO;

public interface QueryTransLogDAO extends MyMapper<StatToGroup>{
	/**
	 * 查询传输日志列表
	 * @param month
	 * @param latnId
	 * @param incomeSource
	 * @param taxtId
	 * @return
	 */
	List<TransLogVO> queryLogList(@Param("month") String month, 
			@Param("latnId") String latnId,
			@Param("incomeSource") String incomeSource,
			@Param("taxtId") String taxtId);
	
	/**
	 * 电子档案下载查询文件名
	 * @param batchId
	 * @return
	 */
	String queryFileName(String batchId);
}
