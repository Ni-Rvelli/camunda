package com.nr.camunda.bpm.core.userdb.identity.config;
import com.guoteng.kidlime.bpm.core.userdb.identity.plugin.*;
import com.guoteng.kidlime.bpm.core.userdb.service.UserIdentityService;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.plugin.AdministratorAuthorizationPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 用户流程引擎配置
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Configuration
public class UserProcessEngineConfig {


    @Bean
    public ProcessEnginePlugin administratorAuthorizationPlugin() {
        AdministratorAuthorizationPlugin administratorAuthorizationPlugin = new AdministratorAuthorizationPlugin();
        // TODO 管理员组别待定
        administratorAuthorizationPlugin.setAdministratorGroupName("admins");
        return administratorAuthorizationPlugin;
    }

    @Bean
    public ProcessEnginePlugin identityProviderPlugin(UserIdentityService userIdentityService) {
        return new UserIdentityProviderPlugin(userIdentityService);
    }
}
