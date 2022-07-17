package com.zqq.web.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.web.menu.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    //获取菜单列表树数据
    List<Menu> getList();
    //查询上级菜单
    List<Menu> getParentList();
    //根据用户id查询权限列表
    List<Menu> getMenuByUserId(Long userId);
    //根据角色id查询权限信息
    List<Menu> getMenuListByRoleId(Long roleId);
    //根据业主的id查询菜单
    List<Menu> getMenuByUserIdForLiveUser(Long userId);
}

