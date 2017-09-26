package com.loushuiyifan.report.serv;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.report.dao.LocalNetDAO;
import com.loushuiyifan.report.exception.ReportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@Service
public class LocalNetService {

    @Autowired
    LocalNetDAO localNetDAO;

    public List<Map> listByUser(Long userId, Integer lvl) {

        //首先 获取所有关联的地市
        List<Organization> relatedList = localNetDAO.listByUserAndLvl(userId, lvl);

        if (relatedList == null || relatedList.size() == 0) {
            throw new ReportException("该用户未关联地市组织");
        }

        //然后拼接参数
        for (Organization o : relatedList) {
            Long id = o.getId();
            String path = o.getParentIds();
            path = path == null ? id + "/%" : path + id + "/%";
            o.setParentIds(path);
        }

        //最后进行判断所属地市 及子集
        List<Map> list = localNetDAO.listByRootAndLvl(relatedList, lvl);

        return list;
    }
}
