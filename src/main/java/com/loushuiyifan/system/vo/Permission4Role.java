package com.loushuiyifan.system.vo;

import lombok.Data;

/**
 * @author 漏水亦凡
 * @create 2017-06-02 11:06.
 */
@Data
public class Permission4Role {
    private Long resourceId;
    private Byte alls;

    private Long id;

    private String name;

    private String url;

    private Long parentId;

    private String parentIds;

    private String permission;

    private Boolean available;

}
