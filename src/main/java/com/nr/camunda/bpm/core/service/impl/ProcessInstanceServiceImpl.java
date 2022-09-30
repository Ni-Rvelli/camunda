package com.nr.camunda.bpm.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;

import com.nr.camunda.bpm.core.constant.ActivityStateEnum;
import com.nr.camunda.bpm.core.constant.ActivityVariablesConstant;
import com.nr.camunda.bpm.core.entity.ActHiProcinst;
import com.nr.camunda.bpm.core.entity.ActHiTaskinst;
import com.nr.camunda.bpm.core.mapper.ProcessInstanceMapper;
import com.nr.camunda.bpm.core.repository.ActHiTaskinstRepository;
import com.nr.camunda.bpm.core.repository.impl.ActivityRepositoryImpl;
import com.nr.camunda.bpm.core.service.ProcessInstanceService;
import com.nr.camunda.bpm.core.service.dto.*;
import com.nr.camunda.bpm.core.service.dto.query.ProcessInstanceQueryDTO;
import com.nr.camunda.bpm.core.utils.ProcessInstanceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流程实例服务实现类
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Slf4j
@Service
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private SysUserNodeService sysUserNodeService;
    @Autowired
    private ActHiTaskinstRepository actHiTaskinstRepository;
    @Autowired
    private ActivityRepositoryImpl activityRepositoryImpl;
    @Autowired
    @Lazy
    private ProcessTaskServiceImpl processTaskServiceImpl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey, String formType, String startUserId, Map<String, Object> variables) {
        variables = ObjectUtil.isEmpty(variables) ? new HashMap<>(16) : variables;
        // 添加全局变量
        this.buildCommonVariables(variables, businessKey, formType, startUserId);
        // 获取对应的流程定义
        ProcessDefinition processDefinition = this.getStartProcessDefinition(processDefinitionKey);
        // 设置流程发起人(取当前用户)
        identityService.setAuthentication(startUserId, null, Arrays.asList(RestContext.getTenantId()));
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), businessKey, variables);
        return processInstance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String processInstanceId, String assigneeUserId, Map<String, Object> variables) {
        variables = ObjectUtil.isEmpty(variables) ? new HashMap<>(16) : variables;
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (CollectionUtils.isEmpty(taskList)) {
            throw new BusinessException(String.format("撤回失败，流程中不存在任务(%s)！", processInstanceId));
        }
        ActivityInstance activityInstance = runtimeService.getActivityInstance(processInstanceId);
        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished().orderByHistoricActivityInstanceEndTime()
                .asc().list();
        String toActId = historicActivityInstanceList.get(0).getActivityId();
        //关闭相关任务
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelActivityInstance(ProcessInstanceUtil.getInstanceIdForActivity(activityInstance, taskList.get(0).getTaskDefinitionKey()))
                .setAnnotation("进行了撤销到节点操作")
                .startBeforeActivity(toActId)
                .setVariables(variables)
                .execute();

        runtimeService.deleteProcessInstance(processInstanceId, String.format("%s 用户执行了撤回操作", assigneeUserId));
    }

    @Override
    public ProcessInstanceDTO findByInstanceId(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (ObjectUtil.isEmpty(historicProcessInstance)) {
            return null;
        }
        ProcessInstanceDTO dto = new ProcessInstanceDTO();
        dto.setApplyUserId(historicProcessInstance.getStartUserId());
        dto.setState(ActivityStateEnum.getEnum(historicProcessInstance.getState()));
        dto.setProcessInstanceId(processInstanceId);
        dto.setStartTime(historicProcessInstance.getStartTime());
        dto.setEndTime(historicProcessInstance.getEndTime());
        dto.setFromType(this.getVariableValue(processInstanceId, ActivityVariablesConstant.BPM_FORM_TYPE));
        return dto;
    }

    @Override
    public QueryPagingResult findByPage(ProcessInstanceQueryDTO queryDTO, Pager pager) {
        pager = ObjectUtil.isEmpty(pager) ? new Pager() : pager;
        if (ObjectUtil.isEmpty(queryDTO)) {
            throw new BusinessException(400, ExceptionCode.INVALID_ARGUMENT_CODE, "查询参数不能为空");
        }
        QueryPagingResult queryPagingResult = activityRepositoryImpl.findProcessInstance(queryDTO, pager);
        List<ActHiProcinst> instList = (List<ActHiProcinst>) queryPagingResult.getItems();
        List<ProcessInstanceListDTO> result = new ArrayList<>(16);
        instList.stream().forEach(inst -> {
            ProcessInstanceListDTO dto = ProcessInstanceMapper.INSTANCE.toProcessInstanceList(inst);
            List<ActHiTaskinst> hiTaskList = actHiTaskinstRepository.findByProcInstId(inst.getProcInstId());
            if (CollectionUtils.isNotEmpty(hiTaskList)) {
                dto.setLastOpTime(hiTaskList.get(0).getEndTime());
            }
            ProcessTaskRuDTO processTaskRuDTO = processTaskServiceImpl.findCurrentTask(inst.getProcInstId());
            if (ObjectUtil.isNotEmpty(processTaskRuDTO)) {
                dto.setCurrentOpUserIds(processTaskRuDTO.getOpUserIds());
            }
            dto.setFromType(this.getVariableValue(inst.getProcInstId(), ActivityVariablesConstant.BPM_FORM_TYPE));
            result.add(dto);
        });
        queryPagingResult.setItems(result);
        return queryPagingResult;
    }

    @Override
    public ProcessInstanceDetailsDTO details(String processInstanceId) {
        ProcessInstanceDTO processInstanceDTO = this.findByInstanceId(processInstanceId);
        if (ObjectUtil.isEmpty(processInstanceDTO)) {
            return null;
        }
        ProcessInstanceDetailsDTO detailsDTO = ProcessInstanceMapper.INSTANCE.toDetails(processInstanceDTO);
        detailsDTO.setHistoryTaskList(processTaskServiceImpl.findHiTaskByProcessInstanceId(processInstanceId));
        ProcessTaskRuDTO processTaskRuDTO = processTaskServiceImpl.findCurrentTask(processInstanceId);
        if (ObjectUtil.isEmpty(processTaskRuDTO)) {
            return detailsDTO;
        }
        detailsDTO.setCurrentTask(processTaskRuDTO);
        detailsDTO.setFeatureTaskList(processTaskServiceImpl.getFeatureTasks(processInstanceId, null));
        return detailsDTO;
    }

    /**
     * 获取启动的流程(支持多租户版本)
     *
     * @param processDefinitionKey
     * @return
     */
    private ProcessDefinition getStartProcessDefinition(String processDefinitionKey) {
        String tenantId = RestContext.getTenantId();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion();
        if (StringUtils.isBlank(tenantId)) {
            processDefinitionQuery.withoutTenantId();
        } else {
            processDefinitionQuery.tenantIdIn(tenantId).includeProcessDefinitionsWithoutTenantId();
        }
        List<ProcessDefinition> processList = processDefinitionQuery.list();
        if (CollectionUtil.isEmpty(processList)) {
            throw new BusinessException(String.format("未找到相关的流程定义(%s)！", processDefinitionKey));
        }
        return processList.size() > 1 ? processList.stream().filter(o -> o.getTenantId() != null).findFirst().get() : processList.get(0);
    }


    /**
     * 组装公用的流程变量
     *
     * @param variables
     * @param businessKey
     * @param formType
     * @param userId
     */
    public void buildCommonVariables(Map<String, Object> variables, String businessKey, String formType, String userId) {
        List<Map<String, Object>> result = activityRepositoryImpl.getListMap(formType, businessKey);
        if (CollectionUtils.isNotEmpty(result)) {
            result.get(0).forEach((k, v) -> variables.put(k, v));
        }
        variables.put(ActivityVariablesConstant.BPM_FORM_BUSINESS_KEY, businessKey);
        variables.put(ActivityVariablesConstant.BPM_FORM_TYPE, formType);
        variables.put(ActivityVariablesConstant.BPM_START_USER_ID, userId);
        variables.put(ActivityVariablesConstant.BPM_START_TENANT_ID, RestContext.getTenantId());
        variables.put(ActivityVariablesConstant.BPM_USER, this.getBpmUserInfo(userId));
    }


    /**
     * 获取bpm用户信息对象
     *
     * @param userIdStr
     * @return
     */
    public ProcessVariableUserDTO getBpmUserInfo(String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        // 角色
        List<Role> roles = userRoleService.findByUserRole(userId);
        // 部门
        List<SysNode> nodes = sysUserNodeService.findDepartByUserId(userId);
        ProcessVariableUserDTO processVariableUserDTO = new ProcessVariableUserDTO();
        processVariableUserDTO.setId(userIdStr);
        if (CollectionUtils.isNotEmpty(roles)) {
            processVariableUserDTO.setRoleIds(StringUtils.join(roles.stream().map(Role::getId).collect(Collectors.toList()), ","));
        }
        if (CollectionUtils.isNotEmpty(nodes)) {
            processVariableUserDTO.setDepartmentIds(StringUtils.join(nodes.stream().map(SysNode::getId).collect(Collectors.toList()), ","));
        }
        processVariableUserDTO.setDirectorLeaderId("");
        processVariableUserDTO.setDepartmentLeaderIds("");
        processVariableUserDTO.setDepartmentChargeIds("");
        return processVariableUserDTO;
    }


    @Override
    public String getVariableValue(String processInstanceId, String variableName) {
        HistoricVariableInstance variable = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName(variableName)
                .singleResult();
        if (ObjectUtil.isNotEmpty(variable) && ObjectUtil.isNotEmpty(variable.getValue())) {
            return variable.getValue().toString();
        }
        return "";
    }
}
