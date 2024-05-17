package com.example.common.enums;

import lombok.Getter;

@Getter
public enum StorageEnum {

    USER_AVATAR_PATH("files/user/avatar/");
    final String desc;

    StorageEnum(String desc) {
        this.desc = desc;
    }
}
