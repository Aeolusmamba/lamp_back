package com.home.lamp.controller;


import com.home.lamp.bean.Result;
import com.home.lamp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/api/user/login")
    public Result login(@RequestBody Map<String, String> map, HttpServletRequest httpServletRequest){
        return userService.login(map.get("username"), map.get("password"), httpServletRequest);
    }

    @RequestMapping(value = "/api/user/menus", method = {RequestMethod.GET})
    public Result returnMenuInfo(@RequestHeader("Authorization") String token) {    //token在请求头里
        token = token.replaceAll(" ", "\\+");
        return userService.returnMenuInfo(token);
    }

    @RequestMapping(value = "/api/user/users", method = {RequestMethod.GET})
    public Result returnUsers(@RequestHeader("Authorization") String token, @RequestParam("query") String query, @RequestParam("pagenum") int pagenum, @RequestParam("pagesize") int pagesize) {    //token在请求头里
        token = token.replaceAll(" ", "\\+");
        return userService.returnUsers(token, query, pagenum, pagesize);
    }

    @RequestMapping(value = "/api/user/addUser", method = {RequestMethod.POST})
    public Result addUser(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> map) {    //token在请求头里
        token = token.replaceAll(" ", "\\+");
        return userService.addUser(token, map.get("username"), map.get("password"), map.get("mobile"));
    }

    @PutMapping("/api/user/{id}/editUser")
    public Result editUser(@RequestHeader("Authorization") String token, @PathVariable("id") int id, @RequestBody Map<String, String> map) {    //token在请求头里
        token = token.replaceAll(" ", "\\+");
        return userService.editUser(token, id, map.get("username"), map.get("mobile"));
    }

    @DeleteMapping("/api/user/{id}/deleteUser")
    public Result deleteUser(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {    //token在请求头里
        token = token.replaceAll(" ", "\\+");
        return userService.deleteUser(token, id);
    }
}
