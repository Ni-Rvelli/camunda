package com.nr.camunda.bpm.core.userdb.identity;

import com.guoteng.kidlime.bpm.core.userdb.service.UserIdentityService;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;

/**
 * @Description:用户服务工厂类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public class UserIdentityProviderFactory implements SessionFactory {

    private UserIdentityService userIdentityService;

    public UserIdentityProviderFactory(UserIdentityService userIdentityService) {
        super();
        this.userIdentityService = userIdentityService;
    }

    @Override
    public Class<?> getSessionType() {
        return ReadOnlyIdentityProvider.class;
    }

    @Override
    public Session openSession() {
        return new UserIdentityProviderSession(this.userIdentityService);
    }

}
