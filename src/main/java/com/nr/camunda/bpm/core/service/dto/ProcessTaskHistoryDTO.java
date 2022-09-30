package com.nr.camunda.bpm.core.service.dto;

import io.swagger.annotations.Api;
import lombok.Data;
import org.camunda.bpm.engine.task.Comment;

import java.io.Serializable;
import java.util.List;

@Data
@Api(tags = "流程历史任务信息实体")
public class ProcessTaskHistoryDTO extends ProcessTaskDTO implements Serializable {

    private static final long serialVersionUID = 4865923838030598446L;

    /**
     * 审批意见
     */
    private List<Comment> commentList;

    /**
     * 流程实例信息
     */
    private ProcessInstanceDTO processInstance;
}

	