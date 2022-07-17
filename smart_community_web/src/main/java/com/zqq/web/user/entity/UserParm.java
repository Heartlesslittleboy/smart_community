package com.zqq.web.user.entity;

import lombok.Data;

@Data
public class UserParm {
    //页容量
    private Long pageSize;
    //当前页
    private Long curentPage;

    private String phone;
    private String loginName;
}

