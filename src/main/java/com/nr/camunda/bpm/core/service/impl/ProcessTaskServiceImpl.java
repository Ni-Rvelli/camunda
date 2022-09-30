package com.nr.camunda.bpm.core.service.impl;

import cn.hutool.core.util.ObjectUtil;

import com.nr.camunda.bpm.core.constant.ActivityVariablesConstant;
import com.nr.camunda.bpm.core.entity.ActHiTaskinst;
import com.nr.camunda.bpm.core.entity.ActRuIdentityLink;
import com.nr.camunda.bpm.core.entity.ActRuTask;
import com.nr.camunda.bpm.core.mapper.ProcessHiTaskMapper;
import com.nr.camunda.bpm.core.mapper.ProcessRuTaskMapper;
import com.nr.camunda.bpm.core.repository.ActHiTaskinstRepository;
import com.nr.camunda.bpm.core.repository.ActRuIdentityLinkRepository;
import com.nr.camunda.bpm.core.repository.impl.ActivityRepositoryImpl;
import com.nr.camunda.bpm.core.service.ProcessInstanceService;
import com.nr.camunda.bpm.core.service.ProcessTaskService;
import com.nr.camunda.bpm.core.service.dto.ProcessInstanceDTO;
import com.nr.camunda.bpm.core.service.dto.ProcessTaskHistoryDTO;
import com.nr.camunda.bpm.core.service.dto.ProcessTaskRuDTO;
import com.nr.camunda.bpm.core.service.dto.query.ProcessTaskHistoryQueryDTO;
import com.nr.camunda.bpm.core.service.dto.query.ProcessTaskRuQueryDTO;
import com.nr.camunda.bpm.core.utils.ProcessInstanceUtil;
import com.nr.camunda.bpm.core.utils.ProcessTaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流程任务服务类实现
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Slf4j
@Service
public class ProcessTaskServiceImpl implements ProcessTaskService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ActivityRepositoryImpl activityRepositoryImpl;
    @Autowired
    private ActRuIdentityLinkRepository actRuIdentityLinkRepository;
    @Autowired
    private ProcessInstanceService processInstanceService;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private ActHiTaskinstRepository actHiTaskinstRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(String taskId, String assigneeUserId, String comment, Map<String, Object> variables,String nextUserIds) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        String processInstancesId = task.getProcessInstanceId();
        // 签收任务
        taskService.claim(taskId, assigneeUserId);
        // 添加审批意见
        if (StringUtils.isNotBlank(comment)) {
            taskService.createComment(taskId, processInstancesId, comment);
        }
        // 完成任务
        taskService.complete(taskId, variables);
        // 设置下个节点审批人
        if(StringUtils.isNotBlank(nextUserIds)){
            this.addTaskCandidateUser(nextUserIds, task.getProcessInstanceId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void back(String currentTaskId, String rejectToTaskKey, String assigneeUserId, String comment, Map<String, Object> variables) {
        variables = ObjectUtil.isEmpty(variables) ? new HashMap<>(16) : variables;
        // 获取当前实例
        Task task = taskService.createTaskQuery()
                .taskId(currentTaskId)
                .singleResult();
        String processInstanceId = task.getProcessInstanceId();
        ActivityInstance activityInstance = runtimeService.getActivityInstance(processInstanceId);

        List<HistoricActivityInstance> historicList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list();
        // 得到需要驳回的节点id
        Optional<HistoricActivityInstance> historicActivityInstanceOptional = historicList.stream().filter(historicActivityInstance ->
                historicActivityInstance.getActivityId().equals(rejectToTaskKey)).findFirst();
        if (!historicActivityInstanceOptional.isPresent()) {
            throw new BusinessException(String.format("未找到指定的驳回节点(%s)！", rejectToTaskKey));
        }
        // 签收任务
        taskService.claim(currentTaskId, assigneeUserId);
        // 添加审批意见
        if (StringUtils.isNotBlank(comment)) {
            taskService.createComment(currentTaskId, processInstanceId, comment);
        }
        // 进行驳回操作
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelActivityInstance(ProcessInstanceUtil.getInstanceIdForActivity(activityInstance, task.getTaskDefinitionKey()))
                .cancelAllForActivity(currentTaskId)
                .setAnnotation("进行了驳回到指定任务节点操作")
                .startBeforeActivity(rejectToTaskKey)
                .setVariables(variables)
                .execute();
        // 设置驳回节点审批人
        this.addTaskCandidateUser(historicActivityInstanceOptional.get().getAssignee(), task.getProcessInstanceId());
    }

    @Override
    public ProcessTaskRuDTO findCurrentTask(String processInstanceId) {
        ProcessTaskRuQueryDTO queryDTO = new ProcessTaskRuQueryDTO();
        queryDTO.setProcessInstanceId(processInstanceId);
        QueryPagingResult queryPagingResult = this.findRuTask(queryDTO, null);
        if (CollectionUtils.isEmpty(queryPagingResult.getItems())) {
            return null;
        }
        List<ProcessTaskRuDTO> result = (List<ProcessTaskRuDTO>) queryPagingResult.getItems();
        ProcessTaskRuDTO dto = result.get(0);
        TaskDefinition taskDefinition = this.getNextTaskDefinition(processInstanceId, null);
        if (ObjectUtil.isNotEmpty(taskDefinition)) {
            Optional<Expression> optional = taskDefinition.getCandidateUserIdExpressions().stream().filter(o ->
                    o.getExpressionText().equals("${" + ActivityVariablesConstant.APPOINT_USER + "}")).findFirst();
            if (optional.isPresent()) {
                dto.setInput(Arrays.asList(ActivityVariablesConstant.APPOINT_USER));
            }
        }
        return dto;
    }

    @Override
    public QueryPagingResult findRuTask(ProcessTaskRuQueryDTO queryDTO, Pager pager) {
        pager = ObjectUtil.isEmpty(pager) ? new Pager() : pager;
        if (ObjectUtil.isEmpty(queryDTO)) {
            throw new BusinessException(400, ExceptionCode.INVALID_ARGUMENT_CODE, "查询参数不能为空");
        }
        QueryPagingResult queryPagingResult = activityRepositoryImpl.findRuTask(queryDTO, pager);
        List<ActRuTask> taskList = (List<ActRuTask>) queryPagingResult.getItems();
        List<ProcessTaskRuDTO> result = new ArrayList<>(16);
        if (CollectionUtils.isNotEmpty(queryPagingResult.getItems())) {
            taskList.stream().forEach(task -> {
                ProcessTaskRuDTO dto = ProcessRuTaskMapper.INSTANCE.toRuTask(task);
                dto.setProcessInstance(processInstanceService.findByInstanceId(task.getProcInstId()));
                dto.setOpUserIds(this.getCurrentOpUserId(task.getId()));
                result.add(dto);
            });
        }
        queryPagingResult.setItems(result);
        return queryPagingResult;
    }

    @Override
    public QueryPagingResult findHistoricTask(ProcessTaskHistoryQueryDTO queryDTO, Pager pager) {
        pager = ObjectUtil.isEmpty(pager) ? new Pager() : pager;
        if (ObjectUtil.isEmpty(queryDTO)) {
            throw new BusinessException(400, ExceptionCode.INVALID_ARGUMENT_CODE, "查询参数不能为空");
        }
        QueryPagingResult queryPagingResult = activityRepositoryImpl.findHistoricTask(queryDTO, pager);
        List<ActHiTaskinst> taskList = (List<ActHiTaskinst>) queryPagingResult.getItems();
        List<ProcessTaskHistoryDTO> result = new ArrayList<>(16);
        if (CollectionUtils.isNotEmpty(queryPagingResult.getItems())) {
            taskList.stream().forEach(task -> {
                ProcessTaskHistoryDTO dto = ProcessHiTaskMapper.INSTANCE.toHistoryTask(task);
                dto.setProcessInstance(processInstanceService.findByInstanceId(task.getProcInstId()));
                dto.setCommentList(taskService.getTaskComments(task.getId()));
                result.add(dto);
            });
        }
        queryPagingResult.setItems(result);
        return queryPagingResult;
    }

    @Override
    public List<ProcessTaskHistoryDTO> findHiTaskByProcessInstanceId(String processInstanceId) {
        ProcessTaskHistoryQueryDTO queryDTO = new ProcessTaskHistoryQueryDTO();
        queryDTO.setProcessInstanceId(processInstanceId);
        Pager pager = new Pager();
        pager.setLimit(Integer.MAX_VALUE);
        QueryPagingResult queryPagingResult = findHistoricTask(queryDTO, pager);
        if (CollectionUtils.isEmpty(queryPagingResult.getItems())) {
            return null;
        }
        return (List<ProcessTaskHistoryDTO>) queryPagingResult.getItems();
    }

    /**
     * 获取任务对应审批人
     *
     * @param taskId
     * @return
     */
    private List<String> getCurrentOpUserId(String taskId) {
        Optional<ActHiTaskinst> actHiTaskinst = actHiTaskinstRepository.findById(taskId);
        if (actHiTaskinst.isPresent() && StringUtils.isNotBlank(actHiTaskinst.get().getAssignee())) {
            return Arrays.asList(actHiTaskinst.get().getAssignee());
        }
        Set<String> userIdSet = new HashSet<>(16);
        List<ActRuIdentityLink> linkList = actRuIdentityLinkRepository.findByTaskId(taskId);
        if (CollectionUtils.isNotEmpty(linkList)) {
            Set<String> userIds = linkList.stream().filter(o -> !StringUtils.isEmpty(o.getUserId())).map(ActRuIdentityLink::getUserId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(userIds)) {
                userIdSet.addAll(userIds);
            }
            Set<String> groupIds = linkList.stream().filter(o -> !StringUtils.isEmpty(o.getGroupId())).map(ActRuIdentityLink::getGroupId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(groupIds)) {
                groupIds.forEach(o -> {
                    List<UserRole> userRoleList = userRoleRepository.findByUserIdAndRoleId(null, Long.valueOf(o));
                    if (CollectionUtils.isNotEmpty(userRoleList)) {
                        userIdSet.addAll(userRoleList.stream().map(t -> String.valueOf(t.getUserId())).collect(Collectors.toSet()));
                    }
                });

            }
        }
        return new ArrayList<>(userIdSet);
    }

    /**
     * 预取未来的节点
     *
     * @param processInstanceId
     * @param condition
     */
    public List<ProcessTaskRuDTO> getFeatureTasks(String processInstanceId, Map<String, Object> condition) {
        condition = ObjectUtil.isEmpty(condition) ? new HashMap<>() : condition;
        condition.putAll(runtimeService.getVariables(processInstanceId));
        List<TaskDefinition> tasks = null;
        try {
            tasks = ProcessTaskUtil.getFeatureTaskInfo(processInstanceId, condition);
        } catch (Exception e) {
            log.error("ProcessTaskServiceImpl getFeatureTasks error", e);
        }
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.EMPTY_LIST;
        }
        List<ProcessTaskRuDTO> result = new ArrayList<>(tasks.size());
        ProcessInstanceDTO processInstanceDTO = processInstanceService.findByInstanceId(processInstanceId);
        tasks.forEach(task -> {
            ProcessTaskRuDTO dto = new ProcessTaskRuDTO();
            dto.setProcessInstance(processInstanceDTO);
            dto.setProcessInstanceId(processInstanceId);
            dto.setTaskDefKey(task.getKey());
            dto.setName(task.getNameExpression().getExpressionText());
            dto.setStartTime(null);
            dto.setEndTime(null);
            dto.setTenantId(processInstanceDTO.getTenantId());
            dto.setOpUserIds(this.getOpUserIds(task));
            result.add(dto);
        });
        return result;
    }

    /**
     * 获取节点审批人
     *
     * @param task
     * @return
     */
    private List<String> getOpUserIds(TaskDefinition task) {
        Set<String> result = new HashSet<>();
        if (ObjectUtil.isEmpty(task)) {
            return Collections.EMPTY_LIST;
        }
        if (CollectionUtils.isNotEmpty(task.getCandidateUserIdExpressions())) {
            task.getCandidateUserIdExpressions().forEach(userIdExpression -> {
                result.add(userIdExpression.getExpressionText());
            });
        }
        if (CollectionUtils.isNotEmpty(task.getCandidateGroupIdExpressions())) {
            task.getCandidateGroupIdExpressions().forEach(groupIdExpression -> {
                Long groupId = Long.valueOf(groupIdExpression.getExpressionText());
                List<UserRole> userRoleList = userRoleRepository.findByUserIdAndRoleId(null, groupId);
                if (CollectionUtils.isNotEmpty(userRoleList)) {
                    result.addAll(userRoleList.stream().map(t -> String.valueOf(t.getUserId())).collect(Collectors.toSet()));
                }
            });
        }
        return new ArrayList<String>(result);
    }


    /**
     * 获取下一个用户任务定义
     * @param processInstanceId
     * @param condition
     * @return
     */
    private TaskDefinition getNextTaskDefinition(String processInstanceId, Map<String, Object> condition) {
        condition = ObjectUtil.isEmpty(condition) ? new HashMap<>() : condition;
        condition.putAll(runtimeService.getVariables(processInstanceId));
        List<TaskDefinition> tasks = ProcessTaskUtil.getNextTaskInfos(processInstanceId, condition);
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        return tasks.get(0);
    }

    /**
     * 设置指定节点审批人
     *
     * @param nextUserIds
     * @param getProcessInstanceId
     */
    private void addTaskCandidateUser(String nextUserIds, String getProcessInstanceId) {
        Task nextTask = taskService.createTaskQuery()
                .processInstanceId(getProcessInstanceId)
                .singleResult();
        if (ObjectUtil.isNotEmpty(nextTask) && StringUtils.isNotBlank(nextTask.getId())) {
            taskService.addCandidateUser(nextTask.getId(), nextUserIds);
        }
    }
}
