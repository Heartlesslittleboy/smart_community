package com.zqq.web.live_house.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.web.live_house.entiry.LiveHouse;
import com.zqq.web.live_house.mapper.LiveHouseMapper;
import com.zqq.web.live_house.service.LiveHouseService;
import org.springframework.stereotype.Service;

@Service
public class LiveHouseServiceImpl extends ServiceImpl<LiveHouseMapper, LiveHouse> implements LiveHouseService {
}
