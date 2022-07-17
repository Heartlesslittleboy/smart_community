package com.zqq.web.role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.web.role.entity.Role;
import com.zqq.web.role.entity.RoleAssignParm;
import com.zqq.web.role.entity.RoleParm;
import com.zqq.web.role.entity.RolePermissionVo;

import java.util.List;

public interface RoleService extends IService<Role> {
    IPage<Role> list(RoleParm parm);
    //分配权限树数据回显查询
    RolePermissionVo getAssignTree(RoleAssignParm parm);
    //分配权限保存
    void saveAssignRole(Long roleId, List<Long> ids);
}