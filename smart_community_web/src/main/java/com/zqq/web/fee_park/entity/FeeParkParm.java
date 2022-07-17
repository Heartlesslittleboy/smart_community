package com.zqq.web.fee_park.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeeParkParm implements Serializable {
    private Long currentPage;
    private Long pageSize;
    private String userName;
    private String parkName;
    private Long userId;
}
