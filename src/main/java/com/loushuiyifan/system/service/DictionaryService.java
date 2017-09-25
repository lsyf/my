package com.loushuiyifan.system.service;

import com.loushuiyifan.common.bean.Dictionary;
import com.loushuiyifan.common.mapper.DictionaryMapper;
import com.loushuiyifan.system.dao.DictionaryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/6/19
 */
@Service
public class DictionaryService {

    @Autowired
    DictionaryMapper dictionaryMapper;

    @Autowired
    DictionaryDAO dictionaryDAO;


    public List<Dictionary> listRoot(String name) {
        name = String.format("%%%s%%", name);
        return dictionaryDAO.listByName(name);
    }

    public List<Dictionary> listNodes(Long id) {
        return dictionaryDAO.listByParentId(id);
    }

    public int add(Dictionary dic) {
        return dictionaryMapper.insertSelective(dic);
    }

    public int update(Dictionary dic) {
        return dictionaryMapper.updateByPrimaryKeySelective(dic);
    }

    public int delete(Long id) {
        //TODO 检查是否被引用

        //先删除kids
        int num = dictionaryDAO.deleteAllKids(id);

        num += dictionaryMapper.deleteByPrimaryKey(id);
        return num;
    }


    /**
     * 根据字典data获取所有子节点
     *
     * @param data
     * @return
     */
    public List<Dictionary> getAllKidsByData(String data) {
        return dictionaryDAO.getAllKidsByData(data);
    }

    /**
     * 根据字典data和子节点名称获取子节点data
     *
     */
    public String getKidDataByName(String pData,String name) {
        return dictionaryDAO.getKidDataByName(pData,name);
    }
}
