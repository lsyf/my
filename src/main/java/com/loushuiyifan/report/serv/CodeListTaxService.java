package com.loushuiyifan.report.serv;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.bean.CodeListTax;
import com.loushuiyifan.report.dao.CodeListTaxDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/10/31
 */
@Service
public class CodeListTaxService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CodeListTaxService.class);

    @Autowired
    CodeListTaxDAO codeListTaxDAO;

    public List<Map> listByType(String type) {
        return codeListTaxDAO.listByType(type);
    }


    /**
     *用来对现有数据 添加 parentIds
     */
    @Transactional
    public void updateCodeListTaxPath() {
        final String type = "_type_";

        Map<String, CodeListTax> map = Maps.newHashMap();//保存的组织列表

        //遍历各个等级组织
        for (int lvl = 0; lvl < 10; lvl++) {
            List<CodeListTax> list = codeListTaxDAO.listFromCodeListTax(lvl, type);


            LOGGER.info("--{}--", lvl);
            LOGGER.info("num: {}", list.size());
            //如果无数据则代表遍历完毕
            if (list.size() == 0) {
                continue;
            }

            //先进行插入组织数据
            for (CodeListTax a : list) {


                // 如果不存在父节点
                if (a.getParentCodeId() == null) {
                    map.put(a.getCodeId(), a);
                    break;
                }

                CodeListTax p = map.get(a.getParentCodeId());
                if (p == null) {
                    throw new RuntimeException("找不到父节点: " + p.getParentCodeId());
                }

                String tempPath = p.getParentIds();
                tempPath = tempPath == null ? "" : tempPath;
                String path = String.format("%s%s/", tempPath, p.getCodeId());
                a.setParentIds(path);

                codeListTaxDAO.updateByPrimaryKeySelective(a);
                map.put(a.getCodeId(), a);
            }

        }
    }



}
