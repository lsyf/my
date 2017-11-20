package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.ExtImportLog;
import com.loushuiyifan.ws.itsm.C4Detail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface ExtImportLogDAO extends MyMapper<ExtImportLog> {


    Long nextvalKey();

    /**
     * 根据logId查找统一月份，地区，状态
     *
     * @param logIds
     * @return
     */
    ExtImportLog selectDistinctData(@Param("logIds") Long[] logIds);

    Map<String, String> calcAmount(@Param("logIds") Long[] logIds,
                                   @Param("month") String month);

    List<C4Detail> calcC4Detail(@Param("logIds") Long[] logIds,
                                @Param("month") String month);

    /**
     * 更改状态
     *
     * @param logIds     流水号列表
     * @param status     改为状态
     * @param initStatus 初始化状态
     * @return 修改数据数
     */
    int updateItsmStatus(@Param("logIds") Long[] logIds,
                         @Param("status") String status,
                         @Param("initStatus") String initStatus);

    int updateItsmInfo(@Param("logIds") Long[] logIds,
                       @Param("itsmOrderNo") String itsmOrderNo,
                       @Param("itsmUrl") String itsmUrl);
}
