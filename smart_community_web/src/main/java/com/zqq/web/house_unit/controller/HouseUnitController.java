package com.zqq.web.house_unit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.house_unit.entity.HouseUnit;
import com.zqq.web.house_unit.entity.HouseUnitParm;
import com.zqq.web.house_unit.service.HouseUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋管理模块
 */
@RestController
@RequestMapping("/api/houseUnit")
public class HouseUnitController {
    @Autowired
    private HouseUnitService houseUnitService;

    /**
     * 根据栋数id查询单元列表
     * @param houseUnit
     * @return
     */
    //@PreAuthorize("hasAuthority('sys:houseUnit:add')" + " || hasAuthority('sys:house:add')")
    @GetMapping("/getUnitListByBuildId")
    public ResultVo getUnitListByBuildId(HouseUnit houseUnit){
        QueryWrapper<HouseUnit> query = new QueryWrapper<>();
        query.lambda().eq(HouseUnit::getBuildId,houseUnit.getBuildId());
        List<HouseUnit> list = houseUnitService.list(query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 获取单元列表
     * @param parm
     * @return
     */
    @GetMapping("/list")
    public ResultVo getList(HouseUnitParm parm){
        IPage<HouseUnit> list = houseUnitService.getList(parm);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增单元
     * @param houseUnit
     * @return
     */
    @PreAuthorize("hasAuthority('sys:houseUnit:add')")
    @PostMapping
    public ResultVo add(@RequestBody HouseUnit houseUnit){
        boolean save = houseUnitService.save(houseUnit);
        if(save){
            return ResultUtils.success("新增单元成功!");
        }
        return ResultUtils.error("新增单元失败!");
    }

    /**
     * 编辑单元
     * @param houseUnit
     * @return
     */
    @PreAuthorize("hasAuthority('sys:houseUnit:edit')")
    @PutMapping
    public ResultVo edit(@RequestBody HouseUnit houseUnit){
        boolean save = houseUnitService.updateById(houseUnit);
        if(save){
            return ResultUtils.success("编辑单元成功!");
        }
        return ResultUtils.error("编辑单元失败!");
    }

    /**
     * 删除单元
     * @param unitId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:houseUnit:delete')")
    @DeleteMapping("/{unitId}")
    public ResultVo edit(@PathVariable("unitId") Long unitId){
        boolean b = houseUnitService.removeById(unitId);
        if(b){
            return ResultUtils.success("删除单元成功!");
        }
        return ResultUtils.error("删除单元失败!");
    }
}

