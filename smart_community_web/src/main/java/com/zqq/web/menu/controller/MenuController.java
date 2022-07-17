package com.zqq.web.menu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.menu.entity.Menu;
import com.zqq.web.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理模块
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    /**
     * 获取权限列表
     * @return
     */
    @GetMapping("/list")
    public ResultVo list(){
        //System.out.println("list");
        List<Menu> list = menuService.getList();
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增
     * @param menu
     * @return
     */
    @PreAuthorize("hasAuthority('sys:menu:add')")
    @PostMapping
    public ResultVo addMenu(@RequestBody Menu menu){
        boolean save = menuService.save(menu);
        if(save){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 编辑
     * @param menu
     * @return
     */
    @PreAuthorize("hasAuthority('sys:menu:edit')")
    @PutMapping
    public ResultVo editMenu(@RequestBody Menu menu){
        boolean save = menuService.updateById(menu);
        if(save){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 删除
     * @param menuId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @DeleteMapping("/{menuId}")
    public ResultVo deleteMenu(@PathVariable("menuId") Long menuId){
        QueryWrapper<Menu> query = new QueryWrapper<>();
        query.lambda().eq(Menu::getParentId, menuId);
        List<Menu> list = menuService.list(query);
        if (list.size() > 0) {
            return ResultUtils.error("该菜单存在下级，不能删除！");
        }
        boolean b = menuService.removeById(menuId);
        if(b){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    /**
     * 获取上级菜单
     * @return
     */
    @PreAuthorize("hasAuthority('sys:menu:add')")
    @GetMapping("/parent")
    public ResultVo getParent(){
        List<Menu> parentList = menuService.getParentList();
        return ResultUtils.success("查询成功",parentList);
    }
}
