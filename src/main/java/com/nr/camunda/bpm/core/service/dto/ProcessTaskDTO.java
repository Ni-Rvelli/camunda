package com.nr.camunda.bpm.core.service.dto;

import io.swagger.annotations.Api;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Api(tags = "流程任务信息实体")
public class ProcessTaskDTO implements Serializable {


    private static final long serialVersionUID = 6535839068901487631L;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 任务编码
     */
    private String taskDefKey;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 审批人id
     */
    private List<String> opUserIds;
}

	