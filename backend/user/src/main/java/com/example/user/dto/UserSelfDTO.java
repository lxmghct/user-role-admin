package com.example.user.dto;

import com.example.user.entity.User;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "UserSelfDTO", description = "用户自身信息DTO")
@AllArgsConstructor
@NoArgsConstructor
public class UserSelfDTO extends BaseDTO<User> {

    // 性别, 0: 男, 1: 女
    private Integer gender;
    // 地址
    private String address;
    // 个人简介
    private String introduction;
    // 电话
    private String phone;
    // 邮箱
    private String email;

}
