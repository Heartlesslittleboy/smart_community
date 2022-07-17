package com.zqq.web.live_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.web.live_park.entity.LivePark;
import com.zqq.web.live_user.entity.AssignHouseParm;
import com.zqq.web.live_user.entity.LiveUser;

public interface LiveUserService extends IService<LiveUser> {
    //保存用户
    void saveLiveUser(LiveUser liveUser);
    //业主列表
    IPage<LiveUser> getList(IPage<LiveUser> page, String userName, String phone);
    //编辑业主
    void editLiveUser(LiveUser liveUser);
    //编辑查询
    LiveUser getUser(Long userId);
    //分配房屋保存
    void assignHouse(AssignHouseParm parm);
    //分配车位保存
    void assignSavePark(LivePark livePark);
    //退房
    void returnHouse(AssignHouseParm parm);
    //退车位
    void returnPark(LivePark livePark);
    //根据用户名查询用户信息
    LiveUser loadUser(String username);

}
