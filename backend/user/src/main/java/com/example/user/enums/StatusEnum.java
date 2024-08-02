package com.example.user.enums;

import com.example.common.interfaces.IStatusCode;
import lombok.Getter;

@Getter
public enum StatusEnum implements IStatusCode {

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_NAME_EXISTS(1002, "用户名已存在"),
    USER_NAME_NOT_EMPTY(1003, "用户名不能为空"),
    USER_INFO_NOT_CHANGED(1004, "用户信息未修改"),
    INVALID_VERIFICATION_CODE(1005, "验证码错误或已失效"),
    USER_NAME_NOT_EXISTS(1006, "用户名不存在"),
    USER_DISABLED(1007, "用户已被冻结"),
    USER_NAME_CONTAINS_ILLEGAL_CHARACTER(1008, "用户名仅支持字母、数字以及.-_"),

    PLATFORM_NOT_EXISTS(1021, "登录系统不存在"),
    PERMISSION_DENIED_FOR_PLATFORM(1022, "无权登录该系统"),

    PASSWORD_ERROR(1031, "密码错误"),
    PASSWORD_SAME(1032, "新密码不能与旧密码相同"),

    OPERATION_FAILED(1041, "操作失败"),

    ROLE_NAME_EXISTS(1051, "角色名已存在"),
    ROLE_NAME_NOT_EMPTY(1052, "角色名不能为空"),
    ROLE_INFO_NOT_CHANGED(1053, "角色信息未修改"),
    ROLE_IN_USE(1054, "角色正在使用中"),
    ROLE_NOT_FOUND(1055, "角色不存在"),

    PERMISSION_NOT_EMPTY(1061, "权限不能为空"),
    PERMISSION_NOT_FOUND(1062, "权限不存在"),
    ;

    private final Integer code;

    private final String message;

    StatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
