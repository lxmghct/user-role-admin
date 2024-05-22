package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.exceptions.CustomRuntimeException;
import com.example.user.entity.Role;

public interface RoleService extends IService<Role> {

    boolean existsByName(String roleName, boolean throwExceptionWhenExists) throws CustomRuntimeException;

}
