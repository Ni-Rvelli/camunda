package com.nr.camunda.bpm.core.userdb.identity.query;

import com.guoteng.kidlime.bpm.core.userdb.identity.UserIdentityProviderSession;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

import java.util.List;


/**
 * @Description: 用户信息查询实现类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public class JpaUserQueryImpl  extends UserQueryImpl {
    private static final long serialVersionUID = 1L;

    public JpaUserQueryImpl() {
        super();
    }

    public JpaUserQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public long executeCount(CommandContext commandContext) {
        final UserIdentityProviderSession provider = getProcessUserIdentityProvider(commandContext);
        return provider.findUserCountByQueryCriteria(this);
    }

    @Override
    public List<User> executeList(CommandContext commandContext, Page page) {
        final UserIdentityProviderSession provider = getProcessUserIdentityProvider(commandContext);
        return provider.findUserByQueryCriteria(this);
    }

    @Override
    public UserQuery desc() {
        return super.desc();
    }

    protected UserIdentityProviderSession getProcessUserIdentityProvider(CommandContext commandContext) {
        return (UserIdentityProviderSession) commandContext.getReadOnlyIdentityProvider();
    }

}
