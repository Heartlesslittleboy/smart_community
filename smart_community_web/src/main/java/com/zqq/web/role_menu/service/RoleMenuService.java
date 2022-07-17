package com.zqq.web.role_menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.web.role_menu.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMenuService extends IService<RoleMenu> {
    //保存权限
    void saveRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
}
