package com.example.user.dto;

import com.example.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel(value = "UserDTO", description = "用户DTO")
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends BaseDTO<User> {

    // 用户id
    @ApiModelProperty(value = "用户id", hidden = true, notes = "用户id, 创建时不需要传")
    private Long id;
    // 用户名
    private String userName;
    // 真实姓名
    private String trueName;
    // 密码
    private String password;
    // 邮箱
    private String email;
    // 性别, 0: 男, 1: 女
    private Integer gender;
    // 地址
    private String address;
    // 个人简介
    private String introduction;
    // 电话
    private String phone;
    // 角色列表
    private List<Integer> roleIds;

    public UserDTO(String userName, String password, List<Integer> roleIds) {
        this.userName = userName;
        this.password = password;
        this.roleIds = roleIds;
    }

    @Override
    protected List<String> getExcludeFields() {
        return new ArrayList<String>() {{
            add("roleIds");
        }};
    }

}
