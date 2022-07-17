package com.zqq.web.house_building.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.house_building.entity.HouseBuilding;
import com.zqq.web.house_building.entity.HouseBuildingParm;
import com.zqq.web.house_building.service.HouseBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 栋数管理模块
 */
@RestController
@RequestMapping("/api/houseBuild")
public class HouseBuildingController {
    @Autowired
    private HouseBuildingService houseBuildingService;

    /**
     * 查询栋数列表
     * @return
     */
    //@PreAuthorize("hasAuthority('sys:houseUnit:add')" + " || hasAuthority('sys:house:add')")
    @GetMapping("/unitList")
    public ResultVo unitList(){
        List<HouseBuilding> list = houseBuildingService.list();
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 查询栋数列表
     * @param parm
     * @return
     */
    @GetMapping("/list")
    public ResultVo list(HouseBuildingParm parm){
        IPage<HouseBuilding> list = houseBuildingService.getList(parm);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增
     * @param houseBuilding
     * @return
     */
    @PreAuthorize("hasAuthority('sys:houseBuilding:add')")
    @PostMapping
    synchronized public ResultVo add(@RequestBody HouseBuilding houseBuilding){
//        System.out.println("开始阻塞");
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        boolean b = houseBuildingService.save(houseBuilding);

        if(b){
            return ResultUtils.success("新增楼栋成功!");
        }
        return ResultUtils.error("新增楼栋失败!");
    }

    /**
     * 编辑
     * @param houseBuilding
     * @return
     */
    @PreAuthorize("hasAuthority('sys:houseBuilding:edit')")
    @PutMapping
    public ResultVo edit(@RequestBody HouseBuilding houseBuilding){
        boolean b = houseBuildingService.updateById(houseBuilding);
        if(b){
            return ResultUtils.success("编辑楼栋成功!");
        }
        return ResultUtils.error("编辑楼栋失败!");
    }

    /**
     * 删除
     * @param buildId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:houseBuilding:delete')")
    @DeleteMapping("/{buildId}")
    public ResultVo delete(@PathVariable("buildId") Long buildId){
        boolean b = houseBuildingService.removeById(buildId);
        if(b){
            return ResultUtils.success("删除楼栋成功!");
        }
        return ResultUtils.error("删除楼栋失败!");
    }
}

