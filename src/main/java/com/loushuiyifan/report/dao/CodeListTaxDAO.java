package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.CodeListTax;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/10/31
 */
public interface CodeListTaxDAO extends MyMapper<CodeListTax> {
    /**
     * 全部的收入来源
     *
     * @param type
     * @return
     */
    List<Map> listByType(String type);

    List<Map<String, String>> listIncomeSourceMap(String type);

    List<CodeListTax> listFromCodeListTax(@Param("lvl") int lvl,
                                          @Param("type") String type);

    /**
     * 收入来源传输日志
     *
     * @param lvl
     * @param type
     * @return
     */
    List<Map<String, String>> listIncomeSourceByLvl(@Param("lvl") int lvl,
                                                    @Param("type") String type);

    /**
     * 根据地市Id获得codeName
     *
     * @param id
     * @return
     */
    String codeNameById(String id);

    List<CodeListTax> listKidsByTypeAndData(@Param("type") String type,
                                            @Param("data") String data);
}
