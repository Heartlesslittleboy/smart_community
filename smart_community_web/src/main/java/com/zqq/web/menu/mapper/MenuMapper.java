package com.zqq.web.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zqq.web.menu.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {
    //根据用户id查询权限列表
    List<Menu> getMenuByUserId(@Param("userId") Long userId);
    //根据业主的id查询菜单
    List<Menu> getMenuByUserIdForLiveUser(@Param("userId") Long userId);
    //根据角色id查询权限信息
    List<Menu> getMenuListByRoleId(@Param("roleId") Long roleId);
}

