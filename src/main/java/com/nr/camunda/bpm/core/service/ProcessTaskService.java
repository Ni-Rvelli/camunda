package com.nr.camunda.bpm.core.service;



import com.nr.camunda.bpm.core.service.dto.ProcessTaskHistoryDTO;
import com.nr.camunda.bpm.core.service.dto.ProcessTaskRuDTO;
import com.nr.camunda.bpm.core.service.dto.query.ProcessTaskHistoryQueryDTO;
import com.nr.camunda.bpm.core.service.dto.query.ProcessTaskRuQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description: 流程任务服务类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public interface ProcessTaskService {

    /**
     * 任务完成
     *
     * @param taskId         任务id
     * @param assigneeUserId 处理人，一般传当前登录用户
     * @param comment        审批意见
     * @param variables      变量参数
     * @param nextUserIds    下个节点审批人，多个以逗号隔开
     */
    void complete(String taskId, String assigneeUserId, String comment, Map<String, Object> variables, String nextUserIds);

    /**
     * 任务驳回
     *
     * @param currentTaskId   当前任务id
     * @param rejectToTaskKey 驳回到对应的节点code
     * @param assigneeUserId  处理人，一般传当前登录用户
     * @param comment         审批意见
     * @param variables       变量参数（流程的可变参数赋值，非必传）
     */
    void back(String currentTaskId, String rejectToTaskKey, String assigneeUserId, String comment, Map<String, Object> variables);

    /**
     * 获取流程当前任务
     *
     * @param processInstanceId
     * @return
     */
    ProcessTaskRuDTO findCurrentTask(String processInstanceId);

    /**
     * 获取运行中的任务
     *
     * @param queryDTO 查询参数 （不能为空）
     * @param pager    分页参数
     * @return
     */
    QueryPagingResult findRuTask(ProcessTaskRuQueryDTO queryDTO, Pager pager);

    /**
     * 获取任务历史记录
     *
     * @param queryDTO
     * @param pager
     * @return
     */
    QueryPagingResult findHistoricTask(ProcessTaskHistoryQueryDTO queryDTO, Pager pager);

    /**
     * 获取流程历史任务信息
     * @param processInstanceId
     * @return
     */
    List<ProcessTaskHistoryDTO> findHiTaskByProcessInstanceId(String processInstanceId);
}
