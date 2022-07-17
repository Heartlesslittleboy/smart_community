package com.zqq.web.role.entity;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionParm {
    private Long roleId;
    List<Long> list;
}
