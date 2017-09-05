package com.loushuiyifan.system.dao;

import com.loushuiyifan.common.bean.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceDAO {
    List<Resource> getResourceByType(String type);

    int countResourceByType(String type);

    int countResource();

    Resource getResourceByTypeAndId(@Param("type") String type,
                                    @Param("resourceId") long resourceId);

    int deleteResourceByTypeAndId(@Param("type") String type,
                                  @Param("resourceId") long resourceId);

    int deleteResourceKids(@Param("type") String type,
                           @Param("paths") String paths);

    int updateAllsByIds(@Param("ids") List<String> ids,
                        @Param("alls") int alls);

    int getRoleByResourceId(@Param("resourceId") long resourceId,
                            @Param("type") String type);

    int getRoleByResourceKids(@Param("path") String path,
                              @Param("type") String type);

    String getResourcePath(@Param("resourceId") long resourceId,
                           @Param("type") String type);
}