package com.loushuiyifan.report.serv;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.report.dao.LocalNetDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.system.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@Service
public class LocalNetService {

    @Autowired
    LocalNetDAO localNetDAO;

    @Autowired
    OrganizationService organizationService;

    /**
     * 通用
     * 根据用户获取本地网信息(所有)
     *
     * @param userId
     * @param lvl
     * @return
     */
    public List<Organization> listAllByUser(Long userId, Integer lvl) {

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
        List<Organization> list = localNetDAO.listByRootAndLvl(relatedList, lvl);

        return list;
    }

    /**
     *
     * 根据用户 获取 所有股份下地市
     *
     * @param userId
     * @return
     */
    public List<Organization> listForC4(Long userId) {
        String type = OrganizationService.TYPE_CITY;
        String data = "0";//该data代表股份值

        //首先根据用户获取所有 相关股份的地市信息
        List<Organization> orgs = localNetDAO.preForC3(userId, data);
        if (orgs == null || orgs.size() == 0) {
            throw new ReportException("无关联股份信息");
        }
        int min = orgs.get(0).getLvl();
        if (min < 3) {
            orgs = organizationService.getAllKidsByData(data, type);
            Organization org = organizationService.getByData(type, data);
            orgs.add(0, org);
        }
        return orgs;
    }
    
    public String getCodeName(String typeCode, String codeId){
    	
    	return localNetDAO.getCodeNameById(typeCode, codeId) ;
    }
}
