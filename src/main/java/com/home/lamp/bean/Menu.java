package com.home.lamp.bean;

//menu实体
public class Menu {
    private int id;
    private int menuId;
    private int menuAuth;
    private String authName;
    private String childrenId;
    private int order1;
    private String path;

    public String getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(String childrenId) {
        this.childrenId = childrenId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getMenuAuth() {
        return menuAuth;
    }

    public void setMenuAuth(int menuAuth) {
        this.menuAuth = menuAuth;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public int getOrder1() {
        return order1;
    }

    public void setOrder1(int order1) {
        this.order1 = order1;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
