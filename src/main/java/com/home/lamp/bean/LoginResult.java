package com.home.lamp.bean;

public class LoginResult extends Result {
    private LoginData data = new LoginData();

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }
}
