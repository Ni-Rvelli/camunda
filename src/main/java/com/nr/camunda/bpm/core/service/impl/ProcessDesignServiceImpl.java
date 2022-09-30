package com.nr.camunda.bpm.core.service.impl;


import com.nr.camunda.bpm.core.service.ProcessDesignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @Description: 流程设计实现类
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Slf4j
@Service("processService")
public class ProcessDesignServiceImpl implements ProcessDesignService {

    @Resource
    private RepositoryService repositoryService;

    @Override
    public Deployment deploy(String fileName, InputStream inputStream, String tenantId) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name(fileName)
                .addInputStream(fileName, inputStream);
        if (StringUtils.isNotBlank(tenantId)) {
            deploymentBuilder.tenantId(tenantId);
        }
        return deploymentBuilder.deploy();
    }

    @Override
    public Deployment getDeploy(String deploymentId) {
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
        return deployment;
    }

}
