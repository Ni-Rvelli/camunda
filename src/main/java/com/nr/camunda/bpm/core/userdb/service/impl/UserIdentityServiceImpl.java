package com.nr.camunda.bpm.core.userdb.service.impl;

import com.guoteng.kidlime.bpm.core.userdb.service.UserIdentityService;
import com.guoteng.kidlime.core.module.institution.account.service.SysAccountService;
import com.guoteng.kidlime.core.module.institution.role.entity.Role;
import com.guoteng.kidlime.core.module.institution.role.repository.impl.RoleRepositoryImpl;
import com.guoteng.kidlime.core.module.institution.role.service.dto.RoleQueryDTO;
import com.guoteng.kidlime.core.module.institution.user.entity.SysUser;
import com.guoteng.kidlime.core.module.institution.user.repository.impl.SysUserRepositoryImpl;
import com.guoteng.kidlime.core.module.institution.user.service.dto.UserInfoQueryDTO;
import com.guoteng.mango.core.util.CollectionUtils;
import com.guoteng.mango.core.util.StringUtils;
import org.camunda.bpm.engine.authorization.Permission;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.camunda.bpm.engine.authorization.Permissions.READ;
import static org.camunda.bpm.engine.authorization.Resources.USER;


/**
 * @Description: 流程用户信息服务类
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Service("userIdentityService")
public class UserIdentityServiceImpl implements UserIdentityService {

    @Autowired
    private SysUserRepositoryImpl sysUserRepositoryImpl;
    @Autowired
    private RoleRepositoryImpl roleRepositoryImpl;
    @Autowired
    private SysAccountService sysAccountService;

    @Override
    public List<User> findUsers(UserQueryImpl userQueryImpl) {
        List<SysUser> sysUserList = sysUserRepositoryImpl.queryByCondition(this.buildUserQueryDTO(userQueryImpl));
        if (CollectionUtils.isEmpty(sysUserList)) {
            return Collections.EMPTY_LIST;
        }
        List<User> result = new ArrayList<>();
        for (SysUser sysUser : sysUserList) {
            User user = transformUser(sysUser);
            if (isAuthenticatedUser(user) || isAuthorized(READ, USER, user.getId())) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public List<Group> findGroups(GroupQueryImpl groupQueryImpl) {
        List<Role> roleList = roleRepositoryImpl.queryByCondition(this.buildGroupQueryDTO(groupQueryImpl));
        if (CollectionUtils.isEmpty(roleList)) {
            return Collections.EMPTY_LIST;
        }
        return roleList.stream().map(o -> transformGroup(o)).collect(Collectors.toList());
    }

    @Override
    public boolean checkPassword(String userName, String password) {
//        SysAccount sysAccount = sysAccountService.findFirstByUsernameAndMobile(userName,null);
//        if (ObjectUtil.isEmpty(sysAccount)||sysAccount.isDisabled()||sysAccount.isLocked()) {
//            return false;
//        }
//        String userPassword = PasswordUtil.encrypt(sysAccount.getUsername(), password, sysAccount.getSalt());
//        if(!sysAccount.getPassword().equals(userPassword)){
//            return false;
//        }
        // TODO 目前不使用后台，暂不实现
        return false;
    }

    /**
     * 验证用户
     * @param user
     * @return
     */
    protected boolean isAuthenticatedUser(User user) {
        if (user.getId() == null) {
            return false;
        }
        return user.getId()
                .equals(Context.getCommandContext().getAuthenticatedUserId());
    }

    /**
     * 是否认证
     * @param permission
     * @param resource
     * @param resourceId
     * @return
     */
    protected boolean isAuthorized(Permission permission, Resource resource, String resourceId) {
        return Context.getCommandContext().getAuthorizationManager()
                .isAuthorized(permission, resource, resourceId);
    }


    /**
     * 组装用户查询参数
     *
     * @param userQueryImpl
     * @return
     */
    private UserInfoQueryDTO buildUserQueryDTO(UserQueryImpl userQueryImpl) {
        UserInfoQueryDTO dto = new UserInfoQueryDTO();
        dto.setId(userQueryImpl.getId());
        dto.setIds(userQueryImpl.getIds());
        if (StringUtils.isNotBlank(userQueryImpl.getFirstName())) {
            dto.setRealname(userQueryImpl.getFirstName());
        }
        if (StringUtils.isNotBlank(userQueryImpl.getLastName())) {
            dto.setRealname(userQueryImpl.getLastName());
        }
        if (StringUtils.isNotBlank(userQueryImpl.getFirstNameLike())) {
            dto.setRealnameLike(userQueryImpl.getFirstNameLike());
        }
        if (StringUtils.isNotBlank(userQueryImpl.getLastNameLike())) {
            dto.setRealnameLike(userQueryImpl.getLastNameLike());
        }
        dto.setEmail(userQueryImpl.getEmail());
        dto.setEmailLike(userQueryImpl.getEmailLike());
        dto.setRoleId(userQueryImpl.getGroupId());
        dto.setTenantId(userQueryImpl.getTenantId());
        return dto;
    }

    /**
     * 组装组别查询餐宿
     *
     * @param groupQueryImpl
     * @return
     */
    private RoleQueryDTO buildGroupQueryDTO(GroupQueryImpl groupQueryImpl) {
        RoleQueryDTO dto = new RoleQueryDTO();
        dto.setId(groupQueryImpl.getId());
        dto.setIds(groupQueryImpl.getIds());
        dto.setName(groupQueryImpl.getName());
        dto.setNameLike(groupQueryImpl.getNameLike());
        dto.setType(groupQueryImpl.getType());
        dto.setUserId(groupQueryImpl.getUserId());
        dto.setTenantId(groupQueryImpl.getTenantId());
        return dto;
    }

    /**
     * 用户对象转换
     *
     * @param sysUser
     * @return
     */
    private User transformUser(SysUser sysUser) {
        UserEntity user = new UserEntity();
        user.setId(String.valueOf(sysUser.getId()));
        user.setEmail(sysUser.getEmail());
        user.setFirstName(sysUser.getRealname());
        user.setLastName(sysUser.getRealname());
        user.setRevision(1);
        return user;
    }

    /**
     * 角色对象转换
     *
     * @param role
     * @return
     */
    private Group transformGroup(Role role) {
        Group group = new GroupEntity();
        group.setId(String.valueOf(role.getId()));
        group.setName(role.getName());
        group.setType("");
        return group;
    }
}
