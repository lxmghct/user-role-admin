package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 角色
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    // 角色名称
    private String name;
    // 角色描述
    private String description;
    // 权限等级
    private Integer level;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
    // 状态, 0: 删除, 1: 启用
    private Integer status;


    public static class Status {
        public static final Integer DELETED = 0;
        public static final Integer ENABLE = 1;
    }

}