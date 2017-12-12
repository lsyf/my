package com.loushuiyifan.task.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.task.bean.FileInfoData;
import com.loushuiyifan.task.bean.RptMsgInfoData;
import com.loushuiyifan.task.bean.StatToTaxData;

public interface RptMsgInfoDataDAO extends MyMapper<RptMsgInfoData>{

	List<FileInfoData> queryFileInfo(FileInfoData temp);
	 //取得file_info的序列
	Long getFileInfoseq();
	//查询批次号
	ArrayList<String> selectBatchIds(String month); 
	ArrayList<String> getStatus(@Param("batchId") String batchId,
                                @Param("month") String month,
                                @Param("type") String type);
	
	List<Map<String,String>> listData(@Param("batchId") String batchId,
                                      @Param("month") String month);
	List<StatToTaxData> listStatTaxForMap(@Param("month") String month,
                                          @Param("remark") String remark);
	void deleteFileInfo(@Param("batchId") String batchId,
			             @Param("month") String month,
			             @Param("type") String type);
	 
	 
	void delRptMsgInfo(@Param("batchId") String batchId,
                       @Param("month") String month,
                       @Param("type") String type);
	 
	void changeStatus(@Param("batchId") String batchId,
                      @Param("month") String month,
                      @Param("status") String status);
	
	void changeStatusById(@Param("sessionId") String sessionId,
                          @Param("status") String status);
	
}
