package com.nr.camunda.bpm.core.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 历史流程实例详情信息
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
public class ProcessInstanceDetailsDTO extends ProcessInstanceDTO implements Serializable {

    private static final long serialVersionUID = -444652416458809484L;

    /**
     * 已完成的任务列表
     */
    private List<ProcessTaskHistoryDTO> historyTaskList;
    /**
     * 当前任务信息
     */
    private ProcessTaskRuDTO currentTask;
    /**
     * 未执行的任务列表
     */
    private List<ProcessTaskRuDTO> featureTaskList;
}
