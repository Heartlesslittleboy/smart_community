package com.zqq.web.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.web.user.entity.User;
import com.zqq.web.user.entity.UserParm;
import com.zqq.web.user_role.entity.UserRole;

public interface UserService extends IService<User> {
    IPage<User> list(UserParm parm);
    //根据用户id查询角色id
    UserRole getRoleByUserId(UserRole userRole);
    //分配角色保存
    void saveRole(UserRole userRole);
    //根据登录名查用户信息
    User loadUser(String username);
}

