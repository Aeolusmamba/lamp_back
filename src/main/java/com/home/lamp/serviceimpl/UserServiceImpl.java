package com.home.lamp.serviceimpl;


import com.home.lamp.bean.*;
import com.home.lamp.dao.UserDao;
import com.home.lamp.service.UserService;
import com.home.lamp.tools.CurrentTime;
import com.home.lamp.tools.IpUtil;
import com.home.lamp.tools.TokenProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Transactional(rollbackFor = RuntimeException.class)
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Override
    public Result login(String username, String password, HttpServletRequest httpServletRequest) {
        LoginResult result = new LoginResult();
        LoginData loginData = new LoginData();
        User user;
        try {
            user = userDao.login(username, password);
            if (user != null && user.getId() != null) {
                loginData.setUsername(username);
                loginData.setRoleName(user.getRoleName());
                Auth auth = userDao.queryAuth(user.getId());
                if (auth == null) {  // 还未关联auth表，则关联一下
                    Integer menuAuth = user.getRoleName().equals("超级管理员") ? 1 : 0;
                    userDao.withAuth(user.getId(), menuAuth);
                    auth = new Auth();
                    auth.setUserId(user.getId());
                }
                //token
                TokenProcessor tokenProcessor = TokenProcessor.getInstance();
                String token = tokenProcessor.makeToken();
                loginData.setToken(token);
                auth.setToken(token);   // 更新token值
                auth.setUpdateTime(CurrentTime.getCurrentTime("yyyy-MM-dd HH:mm:ss"));   // token的更新时间
                //ip
                String ip = IpUtil.getIpAddr(httpServletRequest);
                auth.setIp(ip);   // 更新ip
                userDao.updateAuth(auth);
                result.setData(loginData);
                result.getMeta().setStatus(200);
                result.getMeta().setMsg("登录成功");
            } else {
                result.getMeta().setMsg("用户名或密码错误");
                result.getMeta().setStatus(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        return result;
    }

    @Override
    public Result returnMenuInfo(String token) {
        MenuResult result = new MenuResult();
        MenuData menuData = new MenuData();
        List<Menu> menus;
        Integer menuAuth;
        try {
            menuAuth = userDao.getAuth(token);
            if (menuAuth == null) {
                result.getMeta().setStatus(400);
                result.getMeta().setMsg("token无效");
                return result;
            }
            if (menuAuth == 0) {  //超级管理员
                menus = userDao.adminSelectMenus();
            } else {
                menus = userDao.selectMenus(menuAuth);
            }
            ArrayList<MenuList> menulist = new ArrayList<>(menus.size());
            for (Menu menu : menus) {
                MenuList menuList = new MenuList();
                menuList.setPath(menu.getPath());
                menuList.setOrder(menu.getOrder1());
                menuList.setAuthName(menu.getAuthName());
                menuList.setId(menu.getMenuId());  //menuId对应前端的id
                if (menu.getChildrenId() != null && !menu.getChildrenId().equals("")) {
                    String[] ss = menu.getChildrenId().split(",");
                    for (String string : ss) {
                        //注意：此处子菜单不能再有子菜单，否则算法有问题
                        Menu child = userDao.selectMenu(Integer.parseInt(string));
                        if (child.getMenuAuth() == 0 && menuAuth.equals(1)) {
                            continue;
                        }
                        //装进childList
                        MenuList childList = new MenuList();
                        childList.setAuthName(child.getAuthName());
                        childList.setId(child.getMenuId());
                        childList.setOrder(child.getOrder1());
                        childList.setPath(child.getPath());
                        menuList.getChildren().add(childList);  //添加子菜单
                    }
                }
                menulist.add(menuList);
            }
            menuData.setMenulist(menulist);
        } catch (Exception e) {
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        result.getMeta().setStatus(200);
        result.getMeta().setMsg("查询成功");
        result.setData(menuData);
        return result;
    }

    @Override
    public Result returnUsers(String token, String query, int pagenum, int pagesize) {
        UsersResult result = new UsersResult();
        UsersData usersData = new UsersData();
        Integer menuAuth;
        ArrayList<UserList> userList = new ArrayList<>();
        List<User> adminsBeanList;
        Integer total;
        try {
            menuAuth = userDao.getAuth(token);
            if (menuAuth == null || menuAuth.equals(1)) {  //token无效或越权查询
                result.getMeta().setStatus(400);
                result.getMeta().setMsg("token无效或越权查询");
                result.setData(usersData);
                return result;
            }
            if (query == null || query.equals("")) {
                adminsBeanList = userDao.getAllUsers(pagesize, (pagenum - 1) * pagesize);
                total = userDao.getUsersNum();
            } else {
                adminsBeanList = userDao.getAllUsersByQuery(pagesize, (pagenum - 1) * pagesize, query);
                total = userDao.getUsersNumByQuery(query);
            }
            usersData.setPagenum(pagenum);
            if (!adminsBeanList.isEmpty()) {
                for (User adminsBean : adminsBeanList) {
                    UserList userList1 = new UserList();
                    userList1.setId(adminsBean.getId());
                    userList1.setUsername(adminsBean.getUsername());
                    userList1.setRoleName(adminsBean.getRoleName());
                    userList1.setAddTime(adminsBean.getAddTime());
                    userList1.setMobile(adminsBean.getMobile());
                    userList.add(userList1);
                }
                usersData.setUserList(userList);
            } else {
                result.getMeta().setStatus(400);
                result.getMeta().setMsg("无更多用户");
                result.setData(usersData);
                return result;
            }
            usersData.setTotal(total);
        } catch (Exception e) {
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        result.getMeta().setStatus(200);
        result.getMeta().setMsg("查询成功");
        result.setData(usersData);
        return result;
    }

    @Override
    public Result addUser(String token, String username, String password, String mobile) {
        AddUserResult result = new AddUserResult();
        AddUserData addUserData = new AddUserData();
        Integer menuAuth;
        try{
            menuAuth = userDao.getAuth(token);
            if (menuAuth == null || menuAuth.equals(1)) {  //token无效或越权查询
                result.getMeta().setStatus(400);
                result.getMeta().setMsg("token无效或越权查询");
                return result;
            }
            User insertUser = new User(username, password, "普通管理员", mobile, CurrentTime.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
            userDao.addUser(insertUser);
            if(insertUser.getId() == null){
                result.getMeta().setStatus(400);
                result.getMeta().setMsg("添加用户失败");
                return result;
            }else{
                addUserData.setId(insertUser.getId());
                addUserData.setUsername(insertUser.getUsername());
                addUserData.setRoleName(insertUser.getRoleName());
                addUserData.setMobile(insertUser.getMobile());
                addUserData.setAddTime(insertUser.getAddTime());
            }
        }catch(Exception e){
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        result.setData(addUserData);
        result.getMeta().setMsg("添加成功");
        result.getMeta().setStatus(200);
        return result;
    }

    @Override
    public Result editUser(String token, int id, String username, String mobile) {
        AddUserResult result = new AddUserResult();
        AddUserData addUserData = new AddUserData();
        Integer menuAuth;
        try{
            menuAuth = userDao.getAuth(token);
            if (menuAuth == null || menuAuth.equals(1)) {  //token无效或越权查询
                result.getMeta().setStatus(400);
                result.getMeta().setMsg("token无效或越权查询");
                return result;
            }
            userDao.editUser(id, username, mobile);
            User user = userDao.getUser(id);
            addUserData.setId(user.getId());
            addUserData.setUsername(user.getUsername());
            addUserData.setRoleName(user.getRoleName());
            addUserData.setMobile(user.getMobile());
            addUserData.setAddTime(user.getAddTime());
        }catch (Exception e){
            e.printStackTrace();
            result.getMeta().setMsg(e.getMessage());
            result.getMeta().setStatus(400);
        }
        result.setData(addUserData);
        result.getMeta().setMsg("修改成功");
        result.getMeta().setStatus(200);
        return result;
    }

    @Override
    public Result deleteUser(String token, int id) {
        Result result = new Result();
        Integer menuAuth;
        try{
            menuAuth = userDao.getAuth(token);
            if (menuAuth == null || menuAuth.equals(1)) {  //token无效或越权查询
                result.getMeta().setStatus(400);
                result.getMeta().setMsg("token无效或越权查询");
                return result;
            }
            userDao.deleteUser(id);
        }catch(Exception e){
            e.printStackTrace();
            result.getMeta().setStatus(400);
            result.getMeta().setMsg(e.getMessage());
        }
        result.getMeta().setMsg("删除成功");
        result.getMeta().setStatus(200);
        return result;
    }
}
