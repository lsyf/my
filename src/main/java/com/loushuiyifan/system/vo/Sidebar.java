package com.loushuiyifan.system.vo;


import com.loushuiyifan.common.bean.Menu;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-05-11 8:57.
 */
public class Sidebar {
    Menu menu;
    List<Sidebar> kids;
    boolean isTree;


    public boolean isTree() {
        return isTree;
    }

    public void setTree(boolean tree) {
        isTree = tree;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public List<Sidebar> getKids() {
        return kids;
    }

    public void setKids(List<Sidebar> kids) {
        this.kids = kids;
    }
}
