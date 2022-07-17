package com.zqq.web.house_list.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zqq.web.house_list.entity.HouseList;
import com.zqq.web.house_list.entity.ListParm;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.house_list.service.HouseListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋列表模块
 */
@RestController
@RequestMapping("/api/houseList")
public class HouseListController {
    @Autowired
    private HouseListService houseListService;


    /**
     * 查询列表
     * @param listParm
     * @return
     */
    @RequestMapping("/list")
    public ResultVo getList(ListParm listParm){
        IPage<HouseList> list = houseListService.getList(listParm);
        return ResultUtils.success("房屋列表查询成功",list);
    }

    /**
     * 新增房屋
     * @param houseList
     * @return
     */
    @PreAuthorize("hasAuthority('sys:house:add')")
    @PostMapping
    public ResultVo add(@RequestBody HouseList houseList){
        boolean save = houseListService.save(houseList);
        if(save){
            return ResultUtils.success("新增房屋成功!");
        }
        return ResultUtils.error("新增房屋失败!");
    }

    /**
     * 编辑房屋
     * @param houseList
     * @return
     */
    @PreAuthorize("hasAuthority('sys:house:edit')")
    @PutMapping
    public ResultVo edit(@RequestBody HouseList houseList){
        boolean save = houseListService.updateById(houseList);
        if(save){
            return ResultUtils.success("编辑房屋成功!");
        }
        return ResultUtils.error("编辑房屋失败!");
    }

    /**
     * 删除房屋
     * @param houseId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:house:delete')")
    @DeleteMapping("/{houseId}")
    public ResultVo delete(@PathVariable("houseId") String houseId){
        boolean save = houseListService.removeById(houseId);
        if(save){
            return ResultUtils.success("删除房屋成功!");
        }
        return ResultUtils.error("删除房屋失败!");
    }

    /**
     * 根据单元id获取房屋列表
     * @param houseList
     * @return
     */
    @GetMapping("/getHouseByUnitId")
    public ResultVo getHouseByUnitId(HouseList houseList){
        QueryWrapper<HouseList> query = new QueryWrapper<>();
        query.lambda().eq(HouseList::getUnitId,houseList.getUnitId());
        List<HouseList> list = houseListService.list(query);
        return ResultUtils.success("查询成功",list);
    }

}
