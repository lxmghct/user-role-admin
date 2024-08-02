package com.example.user.enums;

import lombok.Getter;

@Getter
public enum PlatformEnum {

    MANAGEMENT_PLATFORM(1, "管理平台"),
    DISPLAY_PLATFORM(2, "展示平台")
    ;

    // 系统描述，用于在某些注解中使用
    public static final String PLATFORM_DESC = "1:管理平台,2:展示平台";

    private final Integer id;

    private final String name;

    PlatformEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


    /**
     * 根据id获取系统名称
     *
     * @param id id
     */
    public static String getNameById(Integer id) {
        for (PlatformEnum platformEnum : PlatformEnum.values()) {
            if (platformEnum.getId().equals(id)) {
                return platformEnum.getName();
            }
        }
        return null;
    }

}
