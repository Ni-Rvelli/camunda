package com.nr.camunda.bpm.core.userdb.identity.query;

import com.guoteng.kidlime.bpm.core.userdb.identity.UserIdentityProviderSession;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

import java.util.List;

/**
 * @Description: 用户组查询实现类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public class JpaGroupQueryImpl extends GroupQueryImpl {

    private static final long serialVersionUID = 1L;

    public JpaGroupQueryImpl() {
        super();
    }

    public JpaGroupQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }


    @Override
    public long executeCount(CommandContext commandContext) {
        final UserIdentityProviderSession provider = getProcessUserIdentityProvider(commandContext);
        return provider.findGroupCountByQueryCriteria(this);
    }

    @Override
    public List<Group> executeList(CommandContext commandContext, Page page) {
        final UserIdentityProviderSession provider = getProcessUserIdentityProvider(commandContext);
        return provider.findGroupByQueryCriteria(this);
    }

    @Override
    public GroupQuery desc() {
        return super.desc();
    }

    protected UserIdentityProviderSession getProcessUserIdentityProvider(CommandContext commandContext) {
        return (UserIdentityProviderSession) commandContext.getReadOnlyIdentityProvider();
    }

}
