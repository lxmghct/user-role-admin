package com.example.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exceptions.CustomRuntimeException;
import com.example.user.dto.UserDTO;
import com.example.user.entity.User;
import com.example.user.enums.StatusEnum;
import com.example.user.mapper.UserMapper;
import com.example.user.service.UserRoleService;
import com.example.user.service.UserService;
import com.example.user.utils.DataUtils;
import com.example.user.utils.IdGenerator;
import com.example.user.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserRoleService userRoleService;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRoleService userRoleService) {
        this.userMapper = userMapper;
        this.userRoleService = userRoleService;
    }

    @Override
    public List<Map<String, Object>> getUserRoleAndPermissionsByUserId(List<Long> userIds) {
        if (userIds == null || userIds.size() == 0) {
            return new ArrayList<>();
        }
        return userMapper.getUserRoleAndPermissionsByUserId(userIds);
    }

    @Override
    public Page<User> getUserList(String userName, String minCreateTime, String maxCreateTime, String orderBy, String orderMethod, Integer page, Integer pageSize) {
        List<User> userList = userMapper.getUserList(userName, minCreateTime, maxCreateTime, orderBy, orderMethod, (page - 1) * pageSize, pageSize);
        List<Map<String, Object>> list = userMapper.getUserRoleAndPermissionsByUserId(userList.stream().map(User::getId).collect(Collectors.toList()));
        Map<Long, Map<String, Object>> map = list.stream().collect(Collectors.toMap(m -> (Long) m.get("userId"), m -> m));
        for (User user : userList) {
            UserUtils.setUserRoleAndPermissionInfo(user, Collections.singletonList(map.get(user.getId())));
        }
        int total = userMapper.countUserList(userName, minCreateTime, maxCreateTime);
        return DataUtils.getPage(userList, total, page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User addOneUser(UserDTO userInfo) throws CustomRuntimeException {
        if (UserUtils.isUserNameContainsIllegalCharacter(userInfo.getUserName())) {
            throw new CustomRuntimeException(StatusEnum.USER_NAME_CONTAINS_ILLEGAL_CHARACTER);
        }
        // 找出是否有相同的userName
        existsByUserName(userInfo.getUserName(), true);
        User user = userInfo.toEntity(User.class);
        Date date = new Date();
        user.setId(IdGenerator.nextId());
        user.setStatus(User.Status.ENABLE).setCreateTime(date).setUpdateTime(date);
        userMapper.insert(user);
        userRoleService.addUserRole(user.getId(), userInfo.getRoleIds(), false);
        return user;
    }

    @Override
    public Map<String, Object> batchCreateUser(List<UserDTO> userDTOS) {
        if (userDTOS == null || userDTOS.size() == 0) {
            return null;
        }
        // 根据用户名和邮箱查询已有的用户，不允许用户名或者邮箱重复
        List<String> userNameList = userDTOS.stream().map(UserDTO::getUserName).collect(Collectors.toList());
        List<User> existUsers = userMapper.selectList(new QueryWrapper<User>().in("user_name", userNameList));
        // 已存在的用户名和邮箱
        Set<String> existUserNameSet = existUsers.stream().map(User::getUserName).collect(Collectors.toSet());
        // 需要添加的用户
        List<User> addUserList = new ArrayList<>();
        // 重复的用户名和邮箱
        List<String> repeatedUserNameList = new ArrayList<>();
        // 已经添加过的用户名和邮箱，用来防止待创建列表中有重复的用户名和邮箱
        Set<String> addedUserNameSet = new HashSet<>();
        // 用户和角色的对应关系
        List<Map<String, Object>> userRoleList = new ArrayList<>();
        Date date = new Date();
        for (UserDTO userDTO : userDTOS) {
            if (DataUtils.checkEmptyString(userDTO.getUserName())) {
                continue;
            }
            userDTO.setUserName(userDTO.getUserName().trim());
            if (existUserNameSet.contains(userDTO.getUserName()) || addedUserNameSet.contains(userDTO.getUserName())) {
                repeatedUserNameList.add(userDTO.getUserName());
                continue;
            }
            addedUserNameSet.add(userDTO.getUserName());
            User user = userDTO.toEntity(User.class);
            user.setId(IdGenerator.nextId());
            user.setStatus(User.Status.ENABLE).setCreateTime(date).setUpdateTime(date);
            addUserList.add(user);
            userRoleList.add(new HashMap<String, Object>() {{
                put("user", user);
                put("roleIdList", userDTO.getRoleIds());
            }});
        }
        if (addUserList.size() > 0) {
            saveBatch(addUserList);
        }
        // 批量添加用户角色
        for (Map<String, Object> userRole : userRoleList) {
            userRole.put("userId", ((User) userRole.get("user")).getId());
            userRole.remove("user");
        }
        if (userRoleList.size() > 0) {
            userRoleService.batchAddUserRole(userRoleList);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("existingUserNameList", repeatedUserNameList);
        result.put("successUserList", addUserList);
        return result;
    }

    @Override
    public boolean existsByUserName(String userName, Boolean throwExceptionWhenExists) throws CustomRuntimeException {
        if (DataUtils.checkEmptyString(userName)) {
            throw new CustomRuntimeException(StatusEnum.USER_NAME_NOT_EMPTY);
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_name", userName.trim()).ne("status", User.Status.DELETED));
        if (throwExceptionWhenExists == null) {
            return user != null;
        }
        if (throwExceptionWhenExists ^ user == null) {
            throw new CustomRuntimeException(user == null ? StatusEnum.USER_NAME_NOT_EXISTS : StatusEnum.USER_NAME_EXISTS);
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Boolean> batchCheckUserName(List<String> userNames) {
        if (userNames == null || userNames.size() == 0) {
            return Collections.emptyMap();
        }
        List<User> existUsers = userMapper.selectList(new QueryWrapper<User>().in("user_name", userNames).ne("status", User.Status.DELETED));
        Set<String> existUserNameSet = existUsers.stream().map(user -> user.getUserName().trim()).collect(Collectors.toSet());
        Map<String, Boolean> result = new HashMap<>();
        for (String name : userNames) {
            result.put(name, existUserNameSet.contains(name));
        }
        return result;
    }

}
