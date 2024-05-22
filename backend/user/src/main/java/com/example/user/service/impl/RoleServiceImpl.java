package com.example.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exceptions.CustomRuntimeException;
import com.example.user.entity.Role;
import com.example.user.enums.StatusEnum;
import com.example.user.mapper.RoleMapper;
import com.example.user.service.RoleService;
import com.example.user.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;

    @Autowired
    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public boolean existsByName(String roleName, boolean throwExceptionWhenExists) throws CustomRuntimeException {
        if (DataUtils.checkEmptyString(roleName)) {
            throw new CustomRuntimeException(StatusEnum.ROLE_NAME_NOT_EMPTY);
        }
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("name", roleName.trim()).eq("status", Role.Status.ENABLE));
        if (role != null && throwExceptionWhenExists) {
            throw new CustomRuntimeException(StatusEnum.ROLE_NAME_EXISTS);
        }
        return role != null;
    }

}
