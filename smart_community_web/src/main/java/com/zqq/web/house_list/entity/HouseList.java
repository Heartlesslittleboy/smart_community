package com.zqq.web.house_list.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("house_list")
public class HouseList implements Serializable {
    //主键
    @TableId(type = IdType.AUTO)
    private Long houseId;
    //栋数id,不属于house_list表，需要排除
    @TableField(exist = false)
    private Long buildId;
    //单元id
    private Long unitId;
    //房屋编号
    private String houseNum;
    //栋数名称，不属于house_list表，需要排除
    @TableField(exist = false)
    private String name;
    //单元名称，不属于house_list表，需要排除
    @TableField(exist = false)
    private String unitName;
    //使用面积
    private String houseArea;
    //使用状态  0:未使用 1：已使用
    private String status;
}

