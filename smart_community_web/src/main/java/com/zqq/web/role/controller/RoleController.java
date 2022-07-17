package com.zqq.web.role.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.role.entity.*;
import com.zqq.web.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理模块
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表
     * @return
     */
    @GetMapping("/getList")
    public ResultVo getList(){
        List<Role> list = roleService.list();
        return ResultUtils.success("成功",list);
    }
    //查询角色列表
    @GetMapping("/list")
    public ResultVo list(RoleParm parm){
        IPage<Role> list = roleService.list(parm);
        return ResultUtils.success("查询成功!",list);
    }

    /**
     * 新增角色
     * @param role
     * @return
     */
    @PreAuthorize("hasAuthority('sys:role:add')")
    @PostMapping
    public ResultVo addRole(@RequestBody Role role){
        boolean save = roleService.save(role);
        if(save){
            return ResultUtils.success("新增角色成功!");
        }
        return ResultUtils.error("新增角色失败!");
    }

    /**
     * 编辑角色
     * @param role
     * @return
     */
    @PreAuthorize("hasAuthority('sys:role:edit')")
    @PutMapping
    public ResultVo editRole(@RequestBody Role role){
        boolean save = roleService.updateById(role);
        if(save){
            return ResultUtils.success("编辑角色成功!");
        }
        return ResultUtils.error("编辑角色失败!");
    }

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @DeleteMapping("/{roleId}")
    public ResultVo deleteRole(@PathVariable("roleId") Long roleId){
        boolean save = roleService.removeById(roleId);
        if(save){
            return ResultUtils.success("删除角色成功!");
        }
        return ResultUtils.error("删除角色失败!");
    }

    /**
     * 分配权限树回显
     * @param parm
     * @return
     */
    @PreAuthorize("hasAuthority('sys:role:assignMenu')")
    @GetMapping("/getAssignTree")
    public ResultVo getAssignTree(RoleAssignParm parm) {
        RolePermissionVo assignTree = roleService.getAssignTree(parm);
        //System.out.println("getAssignTree");
        return ResultUtils.success("获取成功", assignTree);
    }

    /**
     * 分配权限保存
     * @param parm
     * @return
     */
    @PreAuthorize("hasAuthority('sys:role:assignMenu')")
    @PostMapping("/saveAssignRole")
    public ResultVo saveAssignRole(@RequestBody RolePermissionParm parm){
        roleService.saveAssignRole(parm.getRoleId(),parm.getList());
        return ResultUtils.success("分配权限成功!");
    }
}

