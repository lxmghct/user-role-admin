package com.example.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.enums.CommonStatusEnum;
import com.example.common.exceptions.CustomRuntimeException;
import com.example.common.utils.ServletUtils;
import com.example.common.vo.ResponseVO;
import com.example.user.constant.ApiConstants;
import com.example.user.dto.RoleDTO;
import com.example.user.entity.Permission;
import com.example.user.entity.Role;
import com.example.user.entity.RolePermission;
import com.example.user.enums.StatusEnum;
import com.example.user.service.PermissionService;
import com.example.user.service.RolePermissionService;
import com.example.user.service.RoleService;
import com.example.user.service.UserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(tags = "role-api")
@RequestMapping(ApiConstants.API_PREFIX + "/roles")
public class RoleController {

    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;

    @Autowired
    public RoleController(RoleService roleService, UserRoleService userRoleService,
                RolePermissionService rolePermissionService, PermissionService permissionService) {
            this.roleService = roleService;
            this.userRoleService = userRoleService;
            this.rolePermissionService = rolePermissionService;
        this.permissionService = permissionService;
    }

    /**
     * 添加角色
     *
     * @param roleInfo 角色信息
     */
    @ApiOperation(value = "添加角色")
    @PostMapping
    public ResponseVO<String> addRole(@RequestBody RoleDTO roleInfo) {
        try {
            roleService.existsByName(roleInfo.getName(), true);
        } catch (CustomRuntimeException e) {
            return ResponseVO.error(e.getStatusCode());
        }
        roleService.save(roleInfo.toEntity(Role.class).setStatus(Role.Status.ENABLE)
                .setCreateTime(new Date()).setUpdateTime(new Date()));
        return ResponseVO.success();
    }

    /**
     * 检查角色名是否存在
     *
     * @param name 角色名
     */
    @ApiOperation(value = "检查角色名是否存在")
    @GetMapping("/name/exists")
    public ResponseVO<Boolean> existsByName(@RequestParam @ApiParam(value = "角色名", required = true) String name) {
        try {
            return ResponseVO.success(roleService.existsByName(name, false));
        } catch (CustomRuntimeException e) {
            return ResponseVO.error(e.getStatusCode());
        }
    }

    /**
     * 获取角色列表
     * 由于角色数量不多，所以不分页
     *
     * @param searchContent 搜索内容
     */
    @ApiOperation(value = "获取角色列表")
    @GetMapping
    public ResponseVO<List<Role>> getRoleList(@RequestParam(defaultValue = "") @ApiParam("搜索内容") String searchContent) {
        List<Role> roleList = roleService.list(
                new QueryWrapper<Role>().eq("status", Role.Status.ENABLE).and(
                        wrapper -> wrapper.like("name", searchContent).or().like("description", searchContent)
                ).orderByDesc("create_time"));
        return ResponseVO.success(roleList);
    }

    /**
     * 修改角色信息
     *
     * @param roleInfo 角色信息
     */
    @ApiOperation(value = "修改角色信息")
    @PutMapping
    public ResponseVO<String> updateRole(@RequestBody RoleDTO roleInfo) {
        Role role = roleService.getById(roleInfo.getId());
        if (role == null || !role.getStatus().equals(Role.Status.ENABLE)) {
            return ResponseVO.error(StatusEnum.ROLE_NOT_FOUND);
        }
        try {
            roleService.existsByName(roleInfo.getName(), true);
        } catch (CustomRuntimeException e) {
            return ResponseVO.error(e.getStatusCode());
        }
        if (roleInfo.isNotModified(role)) {
            return ResponseVO.error(StatusEnum.ROLE_INFO_NOT_CHANGED);
        }
        roleInfo.copyDataTo(role);
        role.setUpdateTime(new Date());
        roleService.updateById(role);
        return ResponseVO.success();
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色id列表
     */
    @ApiOperation(value = "批量删除角色")
    @DeleteMapping
    public ResponseVO<String> deleteRoles(@RequestParam List<Integer> ids) {
        if (ids == null || ids.size() == 0) {
            return ResponseVO.error(CommonStatusEnum.BAD_REQUEST);
        }
        List<Map<String, Object>> userIdList = userRoleService.getUserIdsByRoleIds(ids);
        if (userIdList != null && userIdList.size() > 0) {
            return ResponseVO.error(StatusEnum.ROLE_IN_USE);
        }
        // 采用逻辑删除
        List<Role> roleList = (List<Role>) roleService.listByIds(ids);
        roleList.forEach(role -> role.setStatus(Role.Status.DELETED));
        roleService.updateBatchById(roleList);
        return ResponseVO.success();
    }

    /**
     * 获取角色权限列表
     *
     * @param id 角色id
     */
    @ApiOperation(value = "获取角色权限列表")
    @GetMapping("/{id}/permissions")
    public ResponseVO<List<Permission>> getPermissionListOfRole(@PathVariable("id") Integer id) {
        Role role = roleService.getById(id);
        if (role == null || !role.getStatus().equals(Role.Status.ENABLE)) {
            return ResponseVO.error(StatusEnum.ROLE_NOT_FOUND);
        }
        List<Permission> rolePermissionList = rolePermissionService.getPermissionsByRoleId(id);
        return ResponseVO.success(rolePermissionList);
    }

    /**
     * 添加角色权限
     *
     * @param roleId           角色id
     * @param permissionIds 权限id列表
     * @param deleteOld        是否删除原有权限
     */
    @ApiOperation(value = "添加角色权限")
    @PostMapping("/{roleId}/permissions")
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<String> addRolePermissions(@PathVariable("roleId") @ApiParam(value = "角色id", required = true) Integer roleId,
                                                 @RequestParam @ApiParam(value = "权限id列表", required = true) List<Integer> permissionIds,
                                                 @RequestParam @ApiParam(value = "是否删除原有权限", required = true) boolean deleteOld) {
        Role role = roleService.getById(roleId);
        if (role == null || !role.getStatus().equals(Role.Status.ENABLE)) {
            return ResponseVO.error(StatusEnum.ROLE_NOT_FOUND);
        }
        if (permissionIds == null || permissionIds.isEmpty()) {
            return ResponseVO.error(StatusEnum.PERMISSION_NOT_EMPTY);
        }
        List<Permission> permissionList = permissionService.list(new QueryWrapper<Permission>().in("id", permissionIds));
        Set<Integer> permissionIdSet = permissionList.stream().map(Permission::getId).collect(Collectors.toSet());
        if (permissionIds.stream().anyMatch(permissionId -> !permissionIdSet.contains(permissionId))) {
            return ResponseVO.error(StatusEnum.PERMISSION_NOT_FOUND);
        }
        List<Integer> oldPermissionIdList = new ArrayList<>();
        if (deleteOld) {
            rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id", roleId));
        } else {
            oldPermissionIdList = rolePermissionService.list(new QueryWrapper<RolePermission>().eq("role_id", roleId)).stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        }
        List<RolePermission> rolePermissionList = new ArrayList<>();
        for (int permissionId : permissionIds) {
            if (oldPermissionIdList.contains(permissionId)) {
                continue;
            }
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId).setPermissionId(permissionId);
            rolePermissionList.add(rolePermission);
        }
        rolePermissionService.saveBatch(rolePermissionList);
        // 更新用户权限缓存
        List<Map<String, Object>> userIdList = userRoleService.getUserIdsByRoleIds(Collections.singletonList(roleId));
        List<Long> userIds = userIdList.stream().map(map -> (Long) map.get("userId")).collect(Collectors.toList());
        ServletUtils.updatePermission(userIds);
        return ResponseVO.success();
    }

    /**
     * 删除角色权限
     *
     * @param rolePermissionIds 角色权限id列表
     */
    @ApiOperation(value = "删除角色权限")
    @DeleteMapping("role-permissions")
    public ResponseVO<String> deleteRolePermissions(@RequestParam List<Integer> rolePermissionIds) {
        if (rolePermissionIds != null && !rolePermissionIds.isEmpty()) {
            rolePermissionService.removeByIds(rolePermissionIds);
        }
        return ResponseVO.success();
    }

}
