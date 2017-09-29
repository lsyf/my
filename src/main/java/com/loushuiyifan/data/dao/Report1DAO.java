package com.loushuiyifan.data.dao;


import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.data.vo.Report1;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface Report1DAO extends MyMapper<Report1> {

    /**
     * 全省移动固网预算
     *
     * @param start
     * @param end
     * @return
     */
    List<Report1> listProvinceMFBg(@Param("start") String start,
                                   @Param("end") String end);

    /**
     * 预算
     *
     * @param start
     * @param end
     * @return
     */
    List<Map<String, Object>> listBudget(@Param("start") String start,
                                         @Param("end") String end);
}