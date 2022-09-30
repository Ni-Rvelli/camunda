package com.nr.camunda.bpm.core.userdb.identity.plugin;

import com.guoteng.kidlime.bpm.core.userdb.identity.UserIdentityProviderFactory;
import com.guoteng.kidlime.bpm.core.userdb.service.UserIdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;

/**
 * @Description:用户身份服务插件
 * @Author: nirui
 * @Date: 2020-05-27
 */
public class UserIdentityProviderPlugin implements ProcessEnginePlugin {

    private UserIdentityService userIdentityService;

    public UserIdentityProviderPlugin(UserIdentityService userIdentityService) {
        super();
        this.userIdentityService = userIdentityService;
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        UserIdentityProviderFactory userIdentityProviderFactory = new UserIdentityProviderFactory(userIdentityService);
        processEngineConfiguration.setIdentityProviderSessionFactory(userIdentityProviderFactory);
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {

    }


}
