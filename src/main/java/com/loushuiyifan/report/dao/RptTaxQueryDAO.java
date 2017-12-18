package com.loushuiyifan.report.dao;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RptTaxQueryDAO {

	String getLatnIdName(@Param("latnId") String latnId);
    List<Map<String, String>> listAreaForTax();

    List<Map<String, String>> getColumn1();

    List<Map<String, String>> getColumn7(@Param("latnId") String latnId);

    List<Map<String, String>> getRow1();

    List<Map<String, String>> getRow2(@Param("latnId") String latnId);

    List<Map<String, String>> getRow5(@Param("latnId") String latnId);

    List<Map<String, String>> getRow7();

    List<Map<String, String>> getRow8(@Param("latnId") String latnId);


    @MapKey("key")
    Map<String, Map<String, String>> getData1(String month);

    @MapKey("key")
    Map<String, Map<String, String>> getData2(String month);

    @MapKey("key")
    Map<String, Map<String, String>> getData3(String month);

    @MapKey("key")
    Map<String, Map<String, String>> getData4(@Param("month") String month,
                                              @Param("latnId") String latnId);

    @MapKey("key")
    Map<String, Map<String, String>> getData5(String month);

    @MapKey("key")
    Map<String, Map<String, String>> getData6(String month);

    @MapKey("key")
    Map<String, Map<String, String>> getData7(String month);

    @MapKey("key")
    Map<String, Map<String, String>> getData8(String month);
}
