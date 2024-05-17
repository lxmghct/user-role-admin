package com.example.common.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {

    SUPER_ADMIN("超级管理员", "SUPER_ADMIN"),
    ADMIN("管理员", "ADMIN"),
    USER("普通用户", "USER"),
    ;

    final String name;
    final String code;

    RoleEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
