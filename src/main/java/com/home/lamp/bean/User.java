package com.home.lamp.bean;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String roleName;
    private String mobile;
    private String addTime;

    public User(String username, String password, String roleName, String mobile, String addTime) {
        this.username = username;
        this.password = password;
        this.roleName = roleName;
        this.mobile = mobile;
        this.addTime = addTime;
    }

    public User(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
