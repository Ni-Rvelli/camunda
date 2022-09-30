package com.nr.camunda.bpm.core.listener;


import cn.hutool.core.util.ObjectUtil;

import com.nr.camunda.bpm.core.constant.ActivityVariablesConstant;
import com.nr.camunda.bpm.core.service.ProcessInstanceService;
import com.nr.camunda.bpm.core.service.ProcessTaskService;
import com.nr.camunda.bpm.core.service.dto.ProcessTaskHistoryDTO;
import com.nr.camunda.bpm.core.service.dto.ProcessTaskRuDTO;
import com.nr.camunda.bpm.core.service.dto.ProcessVariableConfigDTO;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.camunda.bpm.engine.delegate.TaskListener.EVENTNAME_COMPLETE;
import static org.camunda.bpm.engine.delegate.TaskListener.EVENTNAME_CREATE;


@Component
public class CommonListener {

    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ProcessTaskService processTaskService;
    @Autowired
    private ProcessInstanceService processInstanceService;

    @EventListener
    public void onTaskEvent(DelegateTask taskDelegate) {
        if (EVENTNAME_CREATE.equals(taskDelegate.getEventName())) {
            this.autoComplete(taskDelegate);
        } else if (EVENTNAME_COMPLETE.equals(taskDelegate.getEventName())) {
            // TODO 发送任务结束消息
        }
    }


    @EventListener
    public void onExecutionEvent(DelegateExecution executionDelegate) {
        // TODO 发送流程结束消息
    }

    /**
     * 自动审批 (根据配置判断是否需要自动审批通过)
     *
     * @param taskDelegate
     */
    private void autoComplete(DelegateTask taskDelegate) {
        ProcessTaskRuDTO processTaskRuDTO = processTaskService.findCurrentTask(taskDelegate.getProcessInstanceId());
        if (!this.isNeedAuto(taskDelegate, processTaskRuDTO)) {
            return;
        }
        // 自动审批
        taskService.claim(taskDelegate.getId(), processTaskRuDTO.getOpUserIds().get(0));
        taskService.createComment(taskDelegate.getId(), taskDelegate.getProcessInstanceId(), "系统自动审批");
        taskService.complete(taskDelegate.getId(), null);
    }


    /**
     * 是否需要自动审批
     *
     * @param taskDelegate
     * @param processTaskRuDTO
     * @return
     */
    private boolean isNeedAuto(DelegateTask taskDelegate, ProcessTaskRuDTO processTaskRuDTO) {
        if (ObjectUtil.isEmpty(processTaskRuDTO)
                || CollectionUtils.isEmpty(processTaskRuDTO.getOpUserIds())) {
            return false;
        }
        // 当前任务需要输入参数（不做自动审批）
        if (!CollectionUtils.isEmpty(processTaskRuDTO.getInput())) {
            return false;
        }
        // 历史任务集合
        List<ProcessTaskHistoryDTO> historyList = processTaskService.findHiTaskByProcessInstanceId(taskDelegate.getProcessInstanceId());
        if (CollectionUtils.isEmpty(historyList)) {
            return false;
        }
        ProcessVariableConfigDTO config = this.getBpmConfig(taskDelegate.getProcessInstanceId());
        // 场景一：无需审批，发起即归档
        if (config.getAutoApproval()) {
            return true;
        }
        // 下面的场景只能有唯一的审批人
        String opUserId = processTaskRuDTO.getOpUserIds().get(0);
        if (processTaskRuDTO.getOpUserIds().size() != 1) {
            return false;
        }
        // 场景二：审批人和发起人是同一个人
        String applyUserId = processInstanceService.getVariableValue(taskDelegate.getProcessInstanceId(), ActivityVariablesConstant.BPM_START_USER_ID);
        if (config.getSameApplyApproval() && applyUserId.equals(opUserId)) {
            return true;
        }
        // 场景三：流程中多次出现
        if (config.getMultipleApproval()) {
            Optional<ProcessTaskHistoryDTO> optional = historyList.stream().filter(o -> o.getOpUserIds().contains(opUserId)).findFirst();
            if (optional.isPresent()) {
                return true;
            }
        }
        // 场景四：流程中连续出现
        if (config.getSuccessiveApproval()) {
            ProcessTaskHistoryDTO last = historyList.stream().max(Comparator.comparing(ProcessTaskHistoryDTO::getStartTime)).get();
            if (last.getOpUserIds().contains(opUserId)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取表单类型
     *
     * @param processInstanceId
     * @return
     */
    private ProcessVariableConfigDTO getBpmConfig(String processInstanceId) {
        HistoricVariableInstance variable = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName(ActivityVariablesConstant.BPM_CONFIG)
                .singleResult();
        if (ObjectUtil.isNotEmpty(variable) && ObjectUtil.isNotEmpty(variable.getValue())) {
            return (ProcessVariableConfigDTO) variable.getValue();
        }
        return new ProcessVariableConfigDTO();
    }
}
