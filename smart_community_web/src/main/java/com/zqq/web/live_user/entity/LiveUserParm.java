package com.zqq.web.live_user.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class LiveUserParm implements Serializable {
    //当前页
    private Long currentPage;
    //页容量
    private Long pageSize;
    //姓名
    private String loginName;
    //电话
    private String phone;
}

