package com.loushuiyifan.system.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.mapper.OrganizationMapper;
import com.loushuiyifan.system.dao.OrganizationDAO;
import com.loushuiyifan.system.vo.CodeListTaxDTO;
import com.loushuiyifan.system.vo.UserCompany;
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

    //地市
    public static final String TYPE_CITY = "local_net";
    //部门
    public static final String TYPE_DEPT = "user_dept";

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

    /**
     * 查询所有组织数据
     *
     * @return
     */
    public List<Organization> listAll() {
        return organizationMapper.selectAll();
    }

    /**
     * 组织(及其下级)是否被用户关联
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
     * 根据组织类型查询所有 信息
     *
     * @param type
     * @return
     */
    public List<Organization> listByType(String type) {
        return organizationDAO.listByType(type);
    }


    /**
     * 根据组织类型和lvl 查询(目前用来查询部门信息)
     *
     * @param type
     * @param lvl
     * @return
     */
    public List<Organization> listByTypeAndLvl(String type, int lvl) {
        return organizationDAO.listByTypeAndLvl(type, lvl);
    }


    /**
     * 根据组织类型 和值获取 数据
     *
     * @param type
     * @param data
     * @return
     */
    public Organization getByData(String type, String data) {
        Organization org = new Organization();
        org.setType(type);
        org.setData(data);
        return organizationMapper.selectOne(org);
    }


    public Organization getByName(String type, String name) {
        Organization org = new Organization();
        org.setType(type);
        org.setName(name);
        return organizationMapper.selectOne(org);
    }

    /**
     * 根据父data和type获取所有直属子节点
     */
    public List<Organization> getUnderKidsByData(String pData, String type) {
        return organizationDAO.getUnderKidsByData(pData, type);
    }


    /**
     * 从旧表(code_list_tax)中导入 地市 组织信息
     */
    public void importDataFromCodeListTax() {
        final String type = TYPE_CITY;

        Map<String, Organization> orgs = Maps.newHashMap();//保存的组织列表

        //遍历各个等级组织
        for (int i = 0; i < 10; i++) {
            List<CodeListTaxDTO> list = organizationDAO.listFromCodeListTax(i);

            LOGGER.info("--{}--", i);
            LOGGER.info("num: {}", list.size());
            //如果无数据则代表遍历完毕
            if (list.size() == 0) {
                break;
            }

            //先进行插入组织数据
            for (CodeListTaxDTO t : list) {
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

                    //拼接父节点路径
                    String tempPath = p.getParentIds();
                    tempPath = tempPath == null ? "" : tempPath;
                    String path = String.format("%s%d/", tempPath, p.getId());
                    o.setParentIds(path);
                }
                organizationMapper.insertSelective(o);
                LOGGER.info("save: {}", o.getData());

                //保存后的组织数据 以data值为key存入容器，以备接下来使用
                orgs.put(o.getData(), o);
            }
        }

    }

    /**
     * 从旧表(user_company)中导入 部门 组织信息
     */
    public void importDataFromUserCompany() {
        final String type = TYPE_DEPT;

        //首先插入父节点
        Organization p = new Organization();
        p.setType(type);
        p.setName("所有部门");
        p.setData("0");
        p.setLvl(1);
        organizationMapper.insertSelective(p);
        Long pId = p.getId();
        String pPath = pId + "/";

        LOGGER.info("save parent: {}", p.getName());


        //查询旧表中所有数据
        List<UserCompany> list = organizationDAO.listFromUserCompany();
        for (UserCompany uc : list) {
            Organization o = new Organization();
            o.setType(type);
            o.setName(uc.getTitle());
            o.setData(uc.getCompanyid());
            o.setParentId(pId);
            o.setParentIds(pPath);
            o.setLvl(2);

            organizationMapper.insertSelective(o);
            LOGGER.info("save: {}", o.getName());
        }

    }


}
