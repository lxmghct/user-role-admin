package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 权限
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    // 权限名称
    private String name;
    // 权限代码
    private String code;
    // 权限描述
    private String description;
    // 权限类别 (模块权限、页面权限、操作权限)
    private String classify;
    // 父级权限
    private Integer parentId;
    // 创建时间
    private Date createTime;
    // 系统
    private String system;

}
