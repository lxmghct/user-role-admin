package com.example.common.enums;

import lombok.Getter;

@Getter
public enum PermissionEnum {

    // 一般用户权限
    DISPLAY_BROWSE("展示平台浏览权限", "DISPLAY:BROWSE"),
    USER_PROFILE("用户个人信息相关权限", "USER:PROFILE"),

    // 管理员权限
    MANAGE("进入管理平台的权限", "MANAGE"),
    DATA_MANAGE("展示平台管理权限", "MANAGE:DATA"),
    USER_MANAGE("用户管理权限", "MANAGE:USER"),

    ;

    final String name;
    final String code;

    PermissionEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

}
