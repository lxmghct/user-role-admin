package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 用户
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    // 用户名
    private String userName;
    // 密码
    @JsonIgnore
    private String password;
    // 头像路径
    private String avatarPath;
    // 电话
    private String phone;
    // 邮箱
    private String email;
    // 性别, 0: 男, 1: 女
    private Integer gender;
    // 地址
    private String address;
    // 个人简介
    private String introduction;
    // 真实姓名
    private String trueName;
    // 状态, 0: 禁用, 1: 启用, 2: 删除
    private Integer status;
    // 注册时间
    private Date createTime;
    // 更新时间
    private Date updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private List<String> roleList;

    @TableField(exist = false)
    private List<String> permissionList;

    public String generateAvatarName(String suffix) {
        if (!Arrays.asList(".jpg", ".jpeg", ".png", ".gif").contains(suffix)) {
            suffix = ".jpg";
        }
        return id + "_" + userName + suffix;
    }

    public static class Status {
        // 字符串常量用于放在注解中
        public static final String DISABLED_STR = "0";
        public static final String ENABLED_STR = "1";
        public static final String DELETED_STR = "2";

        public static final Integer DISABLE = Integer.parseInt(DISABLED_STR);
        public static final Integer ENABLE = Integer.parseInt(ENABLED_STR);
        public static final Integer DELETED = Integer.parseInt(DELETED_STR);

        public static final String DESC = DISABLED_STR + ": 禁用, " + ENABLED_STR + ": 启用, " + DELETED_STR + ": 删除";
    }

    public static class Gender {
        public static final Integer MALE = 0;
        public static final Integer FEMALE = 1;
    }

    // 排序字段
    public static final String ORDER_FIELDS = "create_time,user_name,true_name,email,gender,address,introduction,phone,status,update_time";
    public static final List<String> ORDER_FIELD_LIST = Arrays.asList(ORDER_FIELDS.split(","));

}
