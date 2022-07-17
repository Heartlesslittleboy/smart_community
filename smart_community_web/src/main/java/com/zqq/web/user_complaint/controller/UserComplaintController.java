package com.zqq.web.user_complaint.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.user_complaint.entity.UserComplaint;
import com.zqq.web.user_complaint.entity.UserComplaintParm;
import com.zqq.web.user_complaint.service.UserComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 投诉管理模块
 */
@RestController
@RequestMapping("/api/userComplaint")
public class UserComplaintController {
    @Autowired
    private UserComplaintService userComplaintService;

    /**
     * 投诉列表
     * @param parm
     * @return
     */
    @GetMapping("/list")
    public ResultVo getList(UserComplaintParm parm){
        //构造查询条件
        QueryWrapper<UserComplaint> query = new QueryWrapper<>();
        query.lambda().like(UserComplaint::getTitle,parm.getTitle())
                .like(UserComplaint::getComplaintContent,parm.getComplaintContent());
        //构造分页对象
        IPage<UserComplaint> page = new Page<>();
        page.setCurrent(parm.getCurrentPage());
        page.setSize(parm.getPageSize());
        IPage<UserComplaint> list = userComplaintService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增
     * @param userComplaint
     * @return
     */
    @PreAuthorize("hasAuthority('sys:myUserComplaint:add')")
    @PostMapping
    public ResultVo add(@RequestBody UserComplaint userComplaint){
        //设置投诉状态
        userComplaint.setStatus("0");
        //设置投诉时间
        userComplaint.setCreateTime(new Date());
        //入库保存
        boolean save = userComplaintService.save(userComplaint);
        if(save){
            return ResultUtils.success("投诉成功!");
        }
        return ResultUtils.error("投诉失败!");
    }

    /**
     * 编辑
     * @param userComplaint
     * @return
     */
    @PreAuthorize("hasAuthority('sys:myUserComplaint:edit')"+"|| hasAuthority('sys:myUserComplaint:do')")
    @PutMapping
    public ResultVo edit(@RequestBody UserComplaint userComplaint){
        //编辑保存
        boolean save = userComplaintService.updateById(userComplaint);
        if(save){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 删除
     * @param complaintId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:myUserComplaint:delete')")
    @DeleteMapping("/{complaintId}")
    public ResultVo delete(@PathVariable("complaintId") Long complaintId){
        boolean save = userComplaintService.removeById(complaintId);
        if(save){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    /**
     * 我的投诉
     * @param parm
     * @return
     */
    @GetMapping("/myList")
    public ResultVo getMyList(UserComplaintParm parm){
        //构造查询条件
        QueryWrapper<UserComplaint> query = new QueryWrapper<>();
        query.lambda().like(UserComplaint::getTitle,parm.getTitle())
                .like(UserComplaint::getComplaintContent,parm.getComplaintContent())
                .eq(UserComplaint::getUserId,parm.getUserId());
        //构造分页对象
        IPage<UserComplaint> page = new Page<>();
        page.setCurrent(parm.getCurrentPage());
        page.setSize(parm.getPageSize());
        IPage<UserComplaint> list = userComplaintService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }
}
