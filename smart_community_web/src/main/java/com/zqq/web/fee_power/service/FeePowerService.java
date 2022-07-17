package com.zqq.web.fee_power.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.web.fee_power.entity.FeePower;
import com.zqq.web.fee_power.entity.FeePowerParm;

public interface FeePowerService extends IService<FeePower> {
    //新增电费
    void saveFeePower(FeePower feePower);
    //编辑电费
    void updateFeePower(FeePower feePower);
    //查询列表
    IPage<FeePower> getList(FeePowerParm feePowerParm);
}