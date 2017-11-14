package com.loushuiyifan.report.serv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.bean.CodeListTax;
import com.loushuiyifan.report.dao.CodeListTaxDAO;
import com.loushuiyifan.report.vo.ZtreeVO;

/**
 * @author 漏水亦凡
 * @date 2017/10/31
 */
@Service
public class CodeListTaxService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CodeListTaxService.class);

    @Autowired
    CodeListTaxDAO codeListTaxDAO;

    public List<Map> listIncomeSource(String type) {
        return codeListTaxDAO.listByType(type);
    }

    public CodeListTax getIncomeSource(String type,String data) {
        CodeListTax param = new CodeListTax();
        param.setTypeCode(type);
        param.setCodeId(data);
        return codeListTaxDAO.selectOne(param);
    }


    public List<Map> listByType(String type) {
        return codeListTaxDAO.listByType(type);
    }
    
    /**
     * 收入来源传输日志
     * @param type
     * @return
     */
    public List<Map> listTaxSource(int lvl,String type) {
    	List<Map> list = new ArrayList<Map>();
    	Map ma =Maps.newHashMap();
    	ma.put("id","0");
		ma.put("name", "汇总");
		ma.put("data", "0");
		list.add(ma);
    	
		List<Map> l =codeListTaxDAO.codeListTax(lvl, type);
    	for(Map m : l){
    		Map map =Maps.newHashMap();
    		map.put("id", m.get("id").toString());
    		map.put("name", m.get("id") +"-"+m.get("name"));
    		map.put("data", m.get("data").toString());
    		list.add(map);   		
    	}
    	return list;
    }
    
    public String getAreaName(String type){
    	return codeListTaxDAO.codeNameById(type) ;
    }
    
    public String getName(Integer taxtId){
		String str ="";
		if(taxtId != 1){
		str="电子档案";	
		}else{
			str ="凭证";
		}
		return str;
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
