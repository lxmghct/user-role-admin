package com.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.entity.User;
import com.example.user.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Select("<script>" +
            "SELECT `user`.`id` AS `userId` FROM `user` " +
            "LEFT JOIN `user_role` ON `user`.`id` = `user_role`.`user_id` " +
            "WHERE `user`.`status` != " + User.Status.DELETED_STR +
            " AND `user_role`.`role_id` IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "GROUP BY user_id" +
            "</script>")
    List<Map<String, Object>> getUserIdsByRoleIds(@Param("roleIds") List<Integer> roleIds);
}
