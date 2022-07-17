package com.zqq.web.house_building.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.web.house_building.entity.HouseBuilding;
import com.zqq.web.house_building.entity.HouseBuildingParm;

public interface HouseBuildingService extends IService<HouseBuilding> {
    IPage<HouseBuilding> getList(HouseBuildingParm parm);
}
