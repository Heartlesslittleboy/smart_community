package com.zqq.web.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.web.user.entity.User;
import com.zqq.web.user.entity.UserParm;
import com.zqq.web.user.mapper.UserMapper;
import com.zqq.web.user.service.UserService;
import com.zqq.web.user_role.entity.UserRole;
import com.zqq.web.user_role.mapper.UserRoleMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 继承ServiceImpl可以使用MP通用的CRUD
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserRoleMapper userRoleMapper;
    @Override
    public IPage<User> list(UserParm parm) {

        IPage<User> page = new Page<>();
        page.setSize(parm.getPageSize());
        page.setCurrent(parm.getCurentPage());
        QueryWrapper<User> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getPhone())) {
            query.lambda().like(User::getPhone, parm.getPhone());
        }
        if (StringUtils.isNotEmpty(parm.getLoginName())) {
            query.lambda().like(User::getLoginName, parm.getLoginName());
        }
        //System.out.println(query.getSqlSelect());
        return this.baseMapper.selectPage(page, query);
    }

    @Override
    public UserRole getRoleByUserId(UserRole userRole) {
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda().eq(UserRole::getUserId,userRole.getUserId());
        return userRoleMapper.selectOne(query);
    }

    @Override
    @Transactional
    public void saveRole(UserRole userRole) {
        //删除原来的角色
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda().eq(UserRole::getUserId,userRole.getUserId());
        userRoleMapper.delete(query);
        //保存新的角色
        userRoleMapper.insert(userRole);
    }

    @Override
    public User loadUser(String username) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, username);
        return this.baseMapper.selectOne(query);
    }
}

