package com.zqq.web.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.web.menu.entity.MakeMenuTree;
import com.zqq.web.menu.entity.Menu;
import com.zqq.web.menu.service.MenuService;
import com.zqq.web.role.entity.Role;
import com.zqq.web.role.entity.RoleAssignParm;
import com.zqq.web.role.entity.RoleParm;
import com.zqq.web.role.entity.RolePermissionVo;
import com.zqq.web.role.mapper.RoleMapper;
import com.zqq.web.role.service.RoleService;
import com.zqq.web.role_menu.entity.RoleMenu;
import com.zqq.web.role_menu.service.RoleMenuService;
import com.zqq.web.user.entity.User;
import com.zqq.web.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Override
    public IPage<Role> list(RoleParm parm) {
        //构造分页条件
        QueryWrapper<Role> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(parm.getRoleName())){
            query.lambda().like(Role::getRoleName,parm.getRoleName());
        }
        //构造分页对象
        IPage<Role> page = new Page<>();
        page.setCurrent(parm.getCurrentPage());
        page.setSize(parm.getPageSize());
        return this.baseMapper.selectPage(page,query);
    }
    @Override
    public RolePermissionVo getAssignTree(RoleAssignParm parm) {
        //根据用户id查询当前用户信息
        User user = userService.getById(parm.getUserId());
        //查询当前用户的所有权限信息,如果是超级管理员，全部权限
        List<Menu> menuList = null;
        if(user.getIsAdmin().equals("1")){ //如果是超级管理员
            menuList = menuService.list();
        }else{ //不是超级管理员，根据用户id查询权限信息
            menuList =   menuService.getMenuByUserId(parm.getUserId());
        }
        //组装树
        List<Menu> menus = MakeMenuTree.makeTree(menuList, 0L);

        //根据角色id查询角色原来的权限信息
        List<Long> ids = new ArrayList<>();
        List<Menu> listByRoleId = menuService.getMenuListByRoleId(parm.getRoleId());

        Optional.ofNullable(menuList).orElse(new ArrayList<>()).stream().filter(item -> item !=null).forEach(item ->{
            Optional.ofNullable(listByRoleId).orElse(new ArrayList<>()).stream().filter(dom -> dom != null).forEach(dom ->{
                if(item.getMenuId().equals(dom.getMenuId())){
                    ids.add(dom.getMenuId());
                    //return;
                }
            });
        });
        RolePermissionVo vo = new RolePermissionVo();
        vo.setListmenu(menus);
        vo.setCheckList(ids.toArray());
        return vo;
    }
    @Override
    @Transactional
    public void saveAssignRole(Long roleId, List<Long> ids) {
        //保存权限之前，需要 把原来的权限删除
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().eq(RoleMenu::getRoleId,roleId);
        roleMenuService.remove(query);
        //保存新的权限
        roleMenuService.saveRoleMenu(roleId,ids);
    }
}

