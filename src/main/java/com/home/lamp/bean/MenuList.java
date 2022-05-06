package com.home.lamp.bean;

import java.util.ArrayList;

//返回值menuList实体
public class MenuList {
    private int id;
    private String authName;
    private String path;
    ArrayList<MenuList> children = new ArrayList<>();  //默认为空
    private int order;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public ArrayList<MenuList> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<MenuList> children) {
        this.children = children;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
