package com.zqq.web.sys_notice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.sys_notice.entity.SysNotice;
import com.zqq.web.sys_notice.entity.SysNoticeParm;
import com.zqq.web.sys_notice.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 公告管理
 */
@RestController
@RequestMapping("/api/sysNotice")
public class SysNoticeController {
    @Autowired
    private SysNoticeService sysNoticeService;

    /**
     * 查询列表
     * @param parm
     * @return
     */
    @GetMapping("/list")
    public ResultVo getList(SysNoticeParm parm){
        //构造查询条件
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        query.lambda().like(SysNotice::getTitle,parm.getTitle())
                .orderByDesc(SysNotice::getCreateTime);
        //构造分页对象
        IPage<SysNotice> page = new Page<>();
        page.setCurrent(parm.getCurrentPage());
        page.setSize(parm.getPageSize());
        //IPage<SysNotice> list = null;
        IPage<SysNotice> list = sysNoticeService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增
     * @param sysNotice
     * @return
     */
    @PreAuthorize("hasAuthority('sys:noticeList:add')")
    @PostMapping
    public ResultVo add(@RequestBody SysNotice sysNotice){
        sysNotice.setCreateTime(new Date());
        boolean b = sysNoticeService.save(sysNotice);
        if(b){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 编辑
     * @param sysNotice
     * @return
     */
    @PreAuthorize("hasAuthority('sys:noticeList:edit')")
    @PutMapping
    public ResultVo edit(@RequestBody SysNotice sysNotice){
        boolean b = sysNoticeService.updateById(sysNotice);
        if(b){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 删除
     * @param noticeId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:noticeList:delete')")
    @DeleteMapping("/{noticeId}")
    public ResultVo delete(@PathVariable("noticeId") Long noticeId){
        boolean b = sysNoticeService.removeById(noticeId);
        if(b){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}

