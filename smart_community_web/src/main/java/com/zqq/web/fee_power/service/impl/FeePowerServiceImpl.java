package com.zqq.web.fee_power.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.web.fee_power.entity.FeePower;
import com.zqq.web.fee_power.entity.FeePowerParm;
import com.zqq.web.fee_power.mapper.FeePowerMapper;
import com.zqq.web.fee_power.service.FeePowerService;
import com.zqq.web.live_house.mapper.LiveHouseMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FeePowerServiceImpl extends ServiceImpl<FeePowerMapper, FeePower> implements FeePowerService {
    @Resource
    private LiveHouseMapper liveHouseMapper;

    @Override
    public void saveFeePower(FeePower feePower) {
        //保存电费到数据库
        this.baseMapper.insert(feePower);
    }

    @Override
    public void updateFeePower(FeePower feePower) {
        //保存电费到数据库
        this.baseMapper.updateById(feePower);
    }

    @Override
    public IPage<FeePower> getList(FeePowerParm feePowerParm) {
        //构造分页对象
        IPage<FeePower> page = new Page<>();
        page.setCurrent(feePowerParm.getCurrentPage());
        page.setSize(feePowerParm.getPageSize());
        return this.baseMapper.getList(page,feePowerParm.getUserName(),feePowerParm.getHouseNum());
    }
}
