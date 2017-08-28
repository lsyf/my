package com.loushuiyifan.web.service;

import com.loushuiyifan.mybatis.bean.Dictionary;
import com.loushuiyifan.mybatis.mapper.DictionaryMapper;
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


    public List<Dictionary> listRoot(String name) {
        name = String.format("%%%s%%", name);
        return dictionaryMapper.listByName(name);
    }

    public List<Dictionary> listNodes(Long id) {
        return dictionaryMapper.listByParentId(id);
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
        int num = dictionaryMapper.deleteAllKids(id);

        num += dictionaryMapper.deleteByPrimaryKey(id);
        return num;
    }


    /**
     * 根据字典code获取所有子节点
     * @param data
     * @return
     */
    public List<Dictionary> getParameter(String data) {
        return dictionaryMapper.getParameter(data);
    }
}
