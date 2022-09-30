package com.nr.camunda.bpm.core.service;

import org.camunda.bpm.engine.repository.Deployment;

import java.io.InputStream;

/**
 * @Description: 流程服务类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public interface ProcessDesignService {

    /**
     * 流程发布
     * @param fileName
     * @param inputStream
     * @param tenantId
     * @return
     */
    Deployment deploy(String fileName, InputStream inputStream, String tenantId);

    /**
     * 获取流程发布信息
     * @param deploymentId
     * @return
     */
    Deployment getDeploy(String deploymentId);
}
