package com.zqq.web.live_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * 业主表实体映射
 */
@Data
@TableName("live_user")
public class LiveUser implements UserDetails, Serializable {
    @TableId(type = IdType.AUTO)
    private Long userId;
    //姓名
    private String username;
    //联系电话
    private String phone;
    //性别
    private String sex;
    //姓名
    private String loginName;
    //登录密码
    private String password;
    //状态 0:启用 1：禁用
    private String status;
    //角色id不属于业主表，需要排除
    @TableField(exist =  false)
    private Long roleId;
    //不属于业主表，所以需要排除
    @TableField(exist = false)
    private String houseNum;
    //不属于业主表，所以需要排除
    @TableField(exist = false)
    private String houseArea;
    //不属于业主表，所以需要排除
    @TableField(exist = false)
    private String unitName;
    //不属于业主表，所以需要排除
    @TableField(exist = false)
    private String name;
    //不属于业主表，所以需要排除
    @TableField(exist = false)
    private String parkName;
    @TableField(exist = false)
    private Long houseId;
    @TableField(exist = false)
    private Long parkId;
    @TableField(exist = false)
    private String useStatus;
    @TableField(exist = false)
    private String liveStatue;

    //下面的字段，属于spring security的UserDetails的字段
    //帐户是否过期(1 未过期，0已过期)
    private boolean isAccountNonExpired = true;
    //帐户是否被锁定(1 未锁定，0已锁定)
    private boolean isAccountNonLocked = true;
    //密码是否过期(1 未过期，0已过期)
    private boolean isCredentialsNonExpired = true;
    //帐户是否可用(1 可用，0 删除用户)
    private boolean isEnabled = true;
    @TableField(exist = false)
    //用户权限字段的集合
    Collection<? extends GrantedAuthority> authorities;
}


