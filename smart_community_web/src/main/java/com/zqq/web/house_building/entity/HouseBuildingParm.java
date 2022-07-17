package com.zqq.web.house_building.entity;

import lombok.Data;

@Data
public class HouseBuildingParm {
    private String name;
    private String type;
    //页容量
    private Long pageSize;
    //当前页
    private Long currentPage;
}
