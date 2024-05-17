package com.example.common.enums;

import lombok.Getter;

@Getter
public enum HeaderEnum {

    // 存储token
    AUTHORIZATION("Authorization"),
    // 存储用户信息，仅当gateway启用时，由gateway处理token后将token中的用户信息传递给微服务
    LOGIN_USER("login-user"),
    // 存储用户信息，仅当gateway启用时，将用户信息以token-data的形式传递给网关，网关根据token-data生成token
    TOKEN_DATA("token-data"),
    // 移除token标识，由网关或前端根据此标识来移除token
    REMOVE_TOKEN("remove-token"),
    // 更新用户权限，仅当网关启用时，当用户敏感信息或权限发生变化时，通知网关将需要更新权限的用户id写入redis
    UPDATE_PERMISSION("update-permission"),
    ;
    final String value;

    HeaderEnum(String value) {
        this.value = value;
    }
}
