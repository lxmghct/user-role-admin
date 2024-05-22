package com.example.user.dto;

import com.example.user.entity.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "RoleDTO", description = "角色DTO")
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO extends BaseDTO<Role> {

    // 角色id
    @ApiModelProperty(value = "角色id", hidden = true, notes = "角色id, 仅在返回角色信息时使用")
    private Integer id;

    // 角色名
    private String name;

    // 角色描述
    private String description;

    // 权限等级
    private Integer level;

}
