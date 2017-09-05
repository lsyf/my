package com.loushuiyifan.system.dao;


import com.loushuiyifan.common.bean.Dictionary;

import java.util.List;

public interface DictionaryDAO {


    List<Dictionary> listByParentId(long parentId);

    List<Dictionary> listByName(String name);

    int deleteAllKids(long parentId);

    List<Dictionary> getParameter(String data);

}