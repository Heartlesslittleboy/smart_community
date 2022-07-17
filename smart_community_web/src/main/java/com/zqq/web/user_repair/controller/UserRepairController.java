package com.zqq.web.user_repair.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.user_repair.entity.UserRepair;
import com.zqq.web.user_repair.entity.UserRepairParm;
import com.zqq.web.user_repair.service.UserRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 维修管理
 */
@RestController
@RequestMapping("/api/userRepair")
public class UserRepairController {
    @Autowired
    private UserRepairService userRepairService;

    /**
     * 我的维修
     * @param userRepairParm
     * @return
     */
    @GetMapping("/myList")
    public ResultVo getMyList(UserRepairParm userRepairParm){
        //构造查询条件
        QueryWrapper<UserRepair> query = new QueryWrapper<>();
        query.lambda().eq(UserRepair::getUserId,userRepairParm.getUserId())
                .like(UserRepair::getRepairContent,userRepairParm.getRepairContent())
                .orderByDesc(UserRepair::getCommitTime);
        //构造分页对象
        IPage<UserRepair> page = new Page<>();
        page.setSize(userRepairParm.getPageSize());
        page.setCurrent(userRepairParm.getCurrentPage());
        IPage<UserRepair> list = userRepairService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 维修列表
     * @param userRepairParm
     * @return
     */
    @GetMapping("/list")
    public ResultVo getList(UserRepairParm userRepairParm){
        //构造查询条件
        QueryWrapper<UserRepair> query = new QueryWrapper<>();
        query.lambda().like(UserRepair::getRepairContent,userRepairParm.getRepairContent())
                .orderByDesc(UserRepair::getCommitTime);
        //构造分页对象
        IPage<UserRepair> page = new Page<>();
        page.setSize(userRepairParm.getPageSize());
        page.setCurrent(userRepairParm.getCurrentPage());
        IPage<UserRepair> list = userRepairService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增
     * @param userRepair
     * @return
     */
    @PreAuthorize("hasAuthority('sys:myRepair:add')")
    @PostMapping
    public ResultVo add(@RequestBody UserRepair userRepair){
        userRepair.setCommitTime(new Date());
        userRepair.setStatus("0");
        boolean b = userRepairService.save(userRepair);
        if(b){
            return ResultUtils.success("报修成功!");
        }
        return ResultUtils.error("报修失败!");
    }

    /**
     * 编辑
     * @param userRepair
     * @return
     */
    @PreAuthorize("hasAuthority('sys:myRepair:edit')" + " || hasAuthority('sys:repairList:do')")
    @PutMapping
    public ResultVo edit(@RequestBody UserRepair userRepair){
        boolean b = userRepairService.updateById(userRepair);
        if(b){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 删除
     * @param repairId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:myRepair:delete')")
    @DeleteMapping("/{repairId}")
    public ResultVo delete(@PathVariable("repairId") Long repairId){
        boolean b = userRepairService.removeById(repairId);
        if(b){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}

