package com.nr.camunda.bpm.core.service;


import com.nr.camunda.bpm.core.service.dto.ProcessInstanceDTO;
import com.nr.camunda.bpm.core.service.dto.ProcessInstanceDetailsDTO;
import com.nr.camunda.bpm.core.service.dto.query.ProcessInstanceQueryDTO;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.Map;

/**
 * @Description: 流程实例服务类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public interface ProcessInstanceService {

    /**
     * 启动流程
     *
     * @param processDefinitionKey 流程定义key
     * @param businessKey          业务关联key
     * @param formType             表单类型
     * @param variables            变量数据
     * @return
     */
    ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey, String formType, String startUserId, Map<String, Object> variables);

    /**
     * 流程撤销
     *
     * @param processInstanceId 流程id
     * @param assigneeUserId    处理人
     * @param variables         变量参数（流程的可变参数赋值，非必传）
     */
    void cancel(String processInstanceId, String assigneeUserId, Map<String, Object> variables);

    /**
     * 获取流程实例信息（只返回基础信息）
     *
     * @param processInstanceId
     * @return
     */
    ProcessInstanceDTO findByInstanceId(String processInstanceId);

    /**
     * 获取流程实例列表
     *
     * @param queryDTO
     * @param pager
     * @return
     */
    QueryPagingResult findByPage(ProcessInstanceQueryDTO queryDTO, Pager pager);

    /**
     * 获取流程实例详情信息
     *
     * @param processInstanceId
     * @return
     */
    ProcessInstanceDetailsDTO details(String processInstanceId);

    /**
     * 获取流程变量值
     *
     * @param processInstanceId
     * @return
     */
    String getVariableValue(String processInstanceId, String variableName);
}
