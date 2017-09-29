package com.loushuiyifan.data.dao;


import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.data.vo.ProductConfig;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface Report2DAO extends MyMapper<ProductConfig> {

    /**
     * 获取 指定月份 移动 固网总值
     *
     * @param month
     * @return
     */
    Map<String, BigDecimal> getSumMF(String month);


    /**
     * 查询 所有产品配置
     *
     * @return
     */
    List<ProductConfig> listProductConfig();


    /**
     * 获取指定类型的 所有产品数据
     *
     * @return
     */
    Map<String, BigDecimal> listProductData(@Param("type") int type,
                                            @Param("configs") List<ProductConfig> configs,
                                            @Param("month") String month);


}