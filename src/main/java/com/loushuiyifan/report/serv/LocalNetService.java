package com.loushuiyifan.report.serv;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.report.dao.LocalNetDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * 根据用户获取本地网信息
     * (如果该用户为C4,则显示其父级C3)
     *
     * @param userId
     * @return
     */
    public List<Organization> listForC3(Long userId) {

        //首先 获取所有关联的地市
        List<Organization> relatedList = localNetDAO.listByUserAndLvl(userId, 4);

        if (relatedList == null || relatedList.size() == 0) {
            throw new ReportException("该用户未关联地市组织");
        }

        //然后拼接参数
        for (Organization o : relatedList) {
            Long id = o.getId();
            String path = o.getParentIds();
            path = path == null ? id + "/%" : path + id + "/%";
            o.setParentIds(path);
            //除c4其他parentId设为空
            if (o.getLvl() != 4) {
                o.setParentId(-1L);
            }
        }

        //最后进行判断所属地市 及子集
        List<Organization> list = localNetDAO.listForC3(relatedList);

        return list;
    }

    /**
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
            orgs = organizationService.getUnderKidsByData(data, type);
            Organization org = organizationService.getByData(type, data);
            orgs.add(0, org);
        }
        return orgs;
    }


    public String getCodeName(String codeId) {
        String type = OrganizationService.TYPE_CITY;
        Organization org = organizationService.getByData(type, codeId);
        return org == null ? null : org.getName();
    }

    /**
     * 获取地市信息(如果isMulti为true,则返回其下一层节点)
     *
     * @param latnId
     * @param isMulti
     * @return
     */
    public List<Organization> listUnderKids(String latnId, boolean isMulti) {
        String type = OrganizationService.TYPE_CITY;

        List<Organization> list = new ArrayList<>();
        Organization org = organizationService.getByData(type, latnId);
        list.add(org);
        if (isMulti) {
            List<Organization> kids = organizationService.getUnderKidsByData(latnId, type);
            list.addAll(kids);
        }
        return list;
    }

    /**
     * 资金缴拨利润中心
     *
     * @param userId
     * @return
     */
    //TODO利润中心 (股份)
    public List<Map<String, String>> listPrctrName(Long userId) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        List<CommonVO> l = localNetDAO.listNameById();
        //String data =l.get(0).getData();  // 地市Id不为空
        Map<String, String> m = Maps.newHashMap();
        m.put("id", "0");
        m.put("name", "全省");
        m.put("data", "0");
        m.put("lvl", "1");
        list.add(m);
        for (CommonVO co : l) {
            String data = co.getData();
            String name = null;
            if ("2".equals(data)) {
                name = "省本部";
            } else if ("3395".equals(data)) {
                name = "网络科技";
            } else {
                name = co.getName().substring(0, 2);
            }


            List<Map<String, String>> list3 = localNetDAO.listPrctrName(name);
            if (list3.size() != 0) {
                list.addAll(list3);
            }
        }


        return list;

    }

    public List<Map<String, String>> listReportName() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> m = Maps.newHashMap();
        m.put("id", "0");
        m.put("name", "全部");
        m.put("data", "0");
        m.put("lvl", "1");
        list.add(m);
        List<Map<String, String>> li = localNetDAO.listReportName();
        list.addAll(li);

        return list;
    }


}
