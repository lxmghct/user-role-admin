package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.entity.Permission;
import com.example.user.entity.RolePermission;

import java.util.List;

public interface RolePermissionService extends IService<RolePermission> {

    List<Permission> getPermissionsByRoleId(Integer roleId);

}
