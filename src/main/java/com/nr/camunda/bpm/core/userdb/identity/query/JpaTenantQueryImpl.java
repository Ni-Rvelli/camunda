package com.nr.camunda.bpm.core.userdb.identity.query;

import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.TenantQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 用户租户查询实现类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public class JpaTenantQueryImpl extends TenantQueryImpl {

    private static final long serialVersionUID = 1L;

    public JpaTenantQueryImpl() {
        super();
    }

    public JpaTenantQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }


    @Override
    public long executeCount(CommandContext commandContext) {
        return 0;
    }

    @Override
    public List<Tenant> executeList(CommandContext commandContext, Page page) {
        return Collections.emptyList();
    }
}
