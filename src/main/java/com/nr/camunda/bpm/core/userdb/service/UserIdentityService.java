package com.nr.camunda.bpm.core.userdb.service;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.UserQueryImpl;

import java.util.List;

/**
 * @Description: 流程用户信息服务类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public interface UserIdentityService {

    /**
     * 查询用户信息
     *
     * @param userQueryImpl
     * @return
     */
    List<User> findUsers(UserQueryImpl userQueryImpl);

    /**
     * 查询组别信息
     *
     * @param groupQueryImpl
     * @return
     */
    List<Group> findGroups(GroupQueryImpl groupQueryImpl);

    /**
     * 校验密码
     *
     * @param userName
     * @param password
     * @return
     */
    boolean checkPassword(String userName, String password);
}
