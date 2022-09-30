package com.nr.camunda.bpm.core.userdb.identity;

import com.guoteng.kidlime.bpm.core.userdb.identity.query.JpaGroupQueryImpl;
import com.guoteng.kidlime.bpm.core.userdb.identity.query.JpaTenantQueryImpl;
import com.guoteng.kidlime.bpm.core.userdb.identity.query.JpaUserQueryImpl;
import com.guoteng.kidlime.bpm.core.userdb.service.UserIdentityService;
import org.camunda.bpm.engine.BadUserRequestException;
import org.camunda.bpm.engine.identity.*;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Description: 身份服务提供者，针对用户/组的访问
 * @Author: nirui
 * @Date: 2020-05-27
 */
public class UserIdentityProviderSession implements ReadOnlyIdentityProvider {

    private static final Logger logger = LoggerFactory.getLogger(UserIdentityProviderSession.class);

    protected UserIdentityService userIdentityService;

    public UserIdentityProviderSession(UserIdentityService userIdentityService) {
        super();
        this.userIdentityService = userIdentityService;
    }


    @Override
    public User findUserById(String userId) {
        return createUserQuery(Context.getCommandContext())
                .userId(userId)
                .singleResult();
    }

    @Override
    public UserQuery createUserQuery() {
        return new JpaUserQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    @Override
    public UserQuery createUserQuery(CommandContext commandContext) {
        return new JpaUserQueryImpl();
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
        throw new BadUserRequestException(
                "Native user queries are not supported for the JPA identity service provider.");
    }

    public long findUserCountByQueryCriteria(JpaUserQueryImpl query) {
        return userIdentityService.findUsers(query).size();
    }

    public List<User> findUserByQueryCriteria(JpaUserQueryImpl query) {
        return userIdentityService.findUsers(query);
    }

    @Override
    public boolean checkPassword(String userId, String password) {
        if (password == null) {
            return false;
        }
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        return userIdentityService.checkPassword(userId, password);
    }

    @Override
    public Group findGroupById(String groupId) {
        return createGroupQuery(org.camunda.bpm.engine.impl.context.Context.getCommandContext())
                .groupId(groupId)
                .singleResult();
    }

    @Override
    public GroupQuery createGroupQuery() {
        return new JpaGroupQueryImpl(org.camunda.bpm.engine.impl.context.Context.getProcessEngineConfiguration()
                .getCommandExecutorTxRequired());
    }

    @Override
    public GroupQuery createGroupQuery(CommandContext commandContext) {
        return new JpaGroupQueryImpl();
    }

    public long findGroupCountByQueryCriteria(JpaGroupQueryImpl query) {
        return userIdentityService.findGroups(query).size();
    }

    public List<Group> findGroupByQueryCriteria(JpaGroupQueryImpl query) {
        return userIdentityService.findGroups(query);
    }

    @Override
    public Tenant findTenantById(String tenantId) {
        // since multi-tenancy is not supported for the JPA plugin, always return null
        TenantEntity tenant = new TenantEntity();
        tenant.setId("1557254270412988416");
        tenant.setName("test");
        return tenant;
    }

    @Override
    public TenantQuery createTenantQuery() {
        return new JpaTenantQueryImpl(org.camunda.bpm.engine.impl.context.Context.getProcessEngineConfiguration()
                .getCommandExecutorTxRequired());
    }

    @Override
    public TenantQuery createTenantQuery(CommandContext commandContext) {
        return new JpaTenantQueryImpl();
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
