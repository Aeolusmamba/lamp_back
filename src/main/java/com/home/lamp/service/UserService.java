package com.home.lamp.service;

import com.home.lamp.bean.Result;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    Result login(String username, String password, HttpServletRequest httpServletRequest);

    Result returnMenuInfo(String token);

    Result returnUsers(String token, String query, int pagenum, int pagesize);

    Result addUser(String token, String username, String password, String mobile);

    Result editUser(String token, int id, String username, String mobile);

    Result deleteUser(String token, int id);
}
