package com.zqq.web.role_menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zqq.web.role_menu.entity.RoleMenu;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    boolean saveRoleMenu(Long roleId, List<Long> menuIds);
}

