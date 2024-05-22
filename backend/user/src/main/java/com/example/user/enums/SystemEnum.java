package com.example.user.enums;

import lombok.Getter;

@Getter
public enum SystemEnum {

    MANAGEMENT_SYSTEM(1, "管理系统"),
    DISPLAY_SYSTEM(2, "展示系统")
    ;
    private final Integer id;

    private final String name;

    SystemEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // 系统描述，用于在某些注解中使用
    public static final String SYSTEM_DESC = "1:管理系统,2:展示系统";

    /**
     * 根据id获取系统名称
     *
     * @param id id
     */
    public static String getNameById(Integer id) {
        for (SystemEnum systemEnum : SystemEnum.values()) {
            if (systemEnum.getId().equals(id)) {
                return systemEnum.getName();
            }
        }
        return null;
    }

}
