package com.nr.camunda.bpm.core.service.dto;

import io.swagger.annotations.Api;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Api(tags = "流程代办任务信息实体")
public class ProcessTaskRuDTO extends ProcessTaskDTO implements Serializable {


    private static final long serialVersionUID = 4865923838030598446L;

    /**
     * 需要输入的参数
     */
    private List<String> input;

    /**
     * 流程实例信息
     */
    private ProcessInstanceDTO processInstance;

}

	