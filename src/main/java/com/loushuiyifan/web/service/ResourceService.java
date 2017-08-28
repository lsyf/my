package com.loushuiyifan.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loushuiyifan.mybatis.bean.Resource;
import com.loushuiyifan.mybatis.mapper.ResourceMapper;
import com.loushuiyifan.web.bean.DataPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-05-10 15:44.
 */
@Service
public class ResourceService {

    @Autowired
    ResourceMapper resourceMapper;

    /**
     * 分页查询 所有资源
     *
     * @param offset
     * @param limit
     * @param type
     * @return
     */
    public DataPage getResource(int offset, int limit, String type) {
        PageHelper.offsetPage(offset, limit);
        List<Resource> resources = getResources(type);

        PageInfo page = new PageInfo(resources);
        long num = page.getTotal();

        DataPage dp = new DataPage();
        dp.setRows(resources);
        dp.setTotal((int) num);
        return dp;
    }

    /**
     * 查询 类型 所有资源数量
     *
     * @param type
     * @return
     */
    public List<Resource> getResources(String type) {
        List<Resource> resources;
        if ("all".equals(type)) {
            resources = resourceMapper.selectAll();
        } else {
            resources = resourceMapper.getResourceByType(type);
        }
        return resources;
    }

    /**
     * 添加资源
     *
     * @param type
     * @param name
     * @param resourceId
     * @return
     */
    public int addResource(String type, String name, long resourceId) {
        Resource resource = new Resource();
        resource.setName(name);
        resource.setType(type);
        resource.setResourceId(resourceId);
        int num = resourceMapper.insertSelective(resource);
        return num;
    }

    /**
     * 更改资源名称
     *
     * @param type
     * @param resourceId
     * @param name
     * @return
     */
    public int updateResource(String type, Long resourceId, String name) {
        Resource resource = resourceMapper.getResourceByTypeAndId(type, resourceId);
        resource.setName(name);
        int num = resourceMapper.updateByPrimaryKeySelective(resource);
        return num;
    }

    /**
     * 删除某个资源以及其子类
     *
     * @param type
     * @param paths
     */
    public int deleteResourceWithKids(String type, long resourceId, String paths) {
        int num = resourceMapper.deleteResourceByTypeAndId(type, resourceId);
        num += resourceMapper.deleteResourceKids(type, paths);
        return num;
    }

    /**
     * 更新资源 '是否需要权限' 字段
     *
     * @param ids
     * @param alls
     * @return
     */
    public int updateResource(String ids, int alls) {
        if (StringUtils.isEmpty(ids)) {
            return 0;
        }

        List<String> resourceIds = Arrays.asList(ids.split(","));

        if (resourceIds.isEmpty()) {
            return 0;
        }

        return resourceMapper.updateAllsByIds(resourceIds, alls);
    }

    /**
     * 是否该资源关联某个角色(删除校验用)
     *
     * @param resourceId
     * @param type
     * @return
     */
    public boolean isResourceRelatedRole(long resourceId, String type) {
        //首先判断该资源引用数量
        int num = resourceMapper.getRoleByResourceId(resourceId, type);

        //再加上下级资源引用数
        String path = resourceMapper.getResourcePath(resourceId, type);
        path = path + resourceId + "/%";
        num += resourceMapper.getRoleByResourceKids(path, type);


        return num > 0;
    }


}
