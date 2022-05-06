package com.home.lamp.bean;

import java.util.ArrayList;

public class UsersData {
    private ArrayList<UserList> userList = new ArrayList<>();
    private int total;  //总的用户数据
    private int pagenum;

    public ArrayList<UserList> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<UserList> userList) {
        this.userList = userList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPagenum() {
        return pagenum;
    }

    public void setPagenum(int pagenum) {
        this.pagenum = pagenum;
    }
}
