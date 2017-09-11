package com.loushuiyifan.system.vo;

import lombok.Data;

/**
 * @author 漏水亦凡
 * @create 2017-06-02 11:04.
 */
@Data
public class Menu4Role {

    private Long id;

    private String name;

    private String type;

    private String url;

    private Long parentId;

    private String parentIds;

    private Short lvl;

    private Boolean available;

    private Long resourceId;
    private Byte alls;

}
