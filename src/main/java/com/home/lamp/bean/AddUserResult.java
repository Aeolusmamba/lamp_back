package com.home.lamp.bean;

public class AddUserResult extends Result {
    AddUserData data = new AddUserData();

    public AddUserData getData() {
        return data;
    }

    public void setData(AddUserData data) {
        this.data = data;
    }
}
