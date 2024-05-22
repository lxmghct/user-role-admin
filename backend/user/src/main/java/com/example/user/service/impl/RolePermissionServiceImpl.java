package com.example.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.user.entity.Permission;
import com.example.user.entity.RolePermission;
import com.example.user.mapper.RolePermissionMapper;
import com.example.user.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    @Autowired
    public RolePermissionServiceImpl(RolePermissionMapper rolePermissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Integer roleId) {
        return rolePermissionMapper.getPermissionsByRoleId(roleId);
    }
}
