package com.zqq.web.house_list.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.web.house_list.entity.HouseList;
import com.zqq.web.house_list.entity.ListParm;
import com.zqq.web.house_list.mapper.HouseListMapper;
import com.zqq.web.house_list.service.HouseListService;
import org.springframework.stereotype.Service;

@Service
public class HouseListServiceImpl extends ServiceImpl<HouseListMapper, HouseList> implements HouseListService {
    @Override
    public IPage<HouseList> getList(ListParm listParm) {
        //构造分页对象
        IPage<HouseList> page = new Page<>();
        page.setCurrent(listParm.getCurrentPage());
        page.setSize(listParm.getPageSize());
        return this.baseMapper.getList(page,listParm.getBuildName(),listParm.getUnitName(),listParm.getHouseNum(),listParm.getStatus());
    }
}

