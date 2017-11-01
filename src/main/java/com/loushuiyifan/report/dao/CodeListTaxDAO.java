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

    List<Map> listByType(String type);

    List<CodeListTax> listFromCodeListTax(@Param("lvl") int lvl,
                                          @Param("type") String type);
}
