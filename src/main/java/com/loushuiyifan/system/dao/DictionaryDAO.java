package com.loushuiyifan.system.dao;


import com.loushuiyifan.common.bean.Dictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictionaryDAO {


    List<Dictionary> listByParentId(long parentId);

    List<Dictionary> listByName(String name);

    int deleteAllKids(long parentId);

    List<Dictionary> getAllKidsByData(String data);

    String getKidDataByName(@Param("pData") String pData,
                         @Param("name") String name);
}