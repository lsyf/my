package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.CodeListTax;

/**
 * @author 漏水亦凡
 * @date 2017/10/31
 */
public interface CodeListTaxDAO extends MyMapper<CodeListTax> {
	/**
	 * 全部的收入来源
	 * @param type
	 * @return
	 */
    List<Map> listByType(String type);

    List<CodeListTax> listFromCodeListTax(@Param("lvl") int lvl,
                                          @Param("type") String type);
    /**
     * 收入来源传输日志
     * @param lvl
     * @param type
     * @return
     */
    List<Map<String, String>> codeListTax(@Param("lvl") int lvl,
                          @Param("type") String type);
    /**
     * 根据地市Id获得codeName
     * @param type
     * @return
     */
    String codeNameById(String type);

}
