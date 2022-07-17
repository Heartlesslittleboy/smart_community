package com.zqq.web.house_unit.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.web.house_unit.entity.HouseUnit;
import com.zqq.web.house_unit.entity.HouseUnitParm;
import com.zqq.web.house_unit.mapper.HouseUnitMapper;
import com.zqq.web.house_unit.service.HouseUnitService;
import org.springframework.stereotype.Service;

@Service
public class HouseUnitServiceImpl extends ServiceImpl<HouseUnitMapper, HouseUnit> implements HouseUnitService {
    @Override
    public IPage<HouseUnit> getList(HouseUnitParm parm) {
        IPage<HouseUnit> page = new Page<>();
        page.setCurrent(parm.getCurrentPage());
        page.setSize(parm.getPageSize());
        return this.baseMapper.getList(page,parm.getBuildName(),parm.getUnitName());
    }
}

