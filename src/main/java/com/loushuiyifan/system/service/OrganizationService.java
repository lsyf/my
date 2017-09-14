package com.loushuiyifan.system.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.mapper.OrganizationMapper;
import com.loushuiyifan.system.dao.OrganizationDAO;
import com.loushuiyifan.system.vo.CodeListTax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/6/19
 */
@Service
public class OrganizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);

    @Autowired
    OrganizationMapper organizationMapper;

    @Autowired
    OrganizationDAO organizationDAO;


    public List<Organization> listRoot(int lvl) {
        return organizationDAO.listByLvl(lvl);
    }

    public List<Organization> listNodes(Long id) {
        Organization org = organizationMapper.selectByPrimaryKey(id);
        String path = org.getParentIds();
        path = path == null ? id + "/%" : path + id + "/%";
        return organizationDAO.listNodesByPath(path);
    }

    public int add(Organization org) {
        return organizationMapper.insertSelective(org);
    }

    public int update(Organization org) {
        //设置组织类型无法更改
        org.setType(null);
        return organizationMapper.updateByPrimaryKeySelective(org);
    }

    public int delete(Long id, String path) {

        //先删除kids
        int num = organizationDAO.deleteAllKids(path);

        num += organizationMapper.deleteByPrimaryKey(id);
        return num;
    }

    public List<Organization> listAll() {
        return organizationMapper.selectAll();
    }

    /**
     * 组织(及其下级)是否被用户关联
     *
     * @param id
     * @param path
     * @return
     */
    public boolean isOrgRelatedUser(Long id, String path) {
        //首先判断该组织引用数量
        int num = organizationDAO.countUserByOrgId(id);

        //再加上下级组织引用数
        path = path + "%";
        num += organizationDAO.countUserByOrgPath(path);
        return num > 0;
    }

    /**
     * 从旧表中导入 地市 组织信息
     */
    public void importDataFromCodeListTax() {
        final String type = "local_net";

        Map<String, Organization> orgs = Maps.newHashMap();//保存的组织列表
        for (int i = 0; i < 10; i++) {
            //首先获取各个等级组织
            List<CodeListTax> list = organizationDAO.listFromCodeListTax(i);

            LOGGER.info("--{}--", i);
            LOGGER.info("num: {}", list.size());
            //如果无数据则代表遍历完毕
            if (list.size() == 0) {
                break;
            }

            //先进行插入数据
            for (CodeListTax t : list) {
                Organization o = new Organization();
                o.setName(t.getCodeName());
                o.setData(t.getCodeId());
                o.setType(type);
                int lvl = t.getCodeLevel();
                o.setLvl(lvl + 1);//由于源表中lvl从0开始，而新策略则为1是根节点
                if (lvl != 0) {//如果lvl不为0，则进行插入父节点和父节点路径
                    String parentCodeId = t.getParentCodeId();

                    //从以保存的容器中寻找父节点
                    Organization p = orgs.get(parentCodeId);
                    o.setParentId(p.getId());

                    //设置父节点路径
                    String tempPath = p.getParentIds();
                    tempPath = tempPath == null ? "" : tempPath;
                    String path = String.format("%s%d/", tempPath, p.getId());
                    o.setParentIds(path);
                }
                organizationMapper.insertSelective(o);
                LOGGER.info("save: {}", o.getData());
                //存入容器
                orgs.put(o.getData(), o);
            }
        }


    }
}
