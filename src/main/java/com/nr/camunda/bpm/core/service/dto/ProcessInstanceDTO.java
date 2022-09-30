package com.nr.camunda.bpm.core.service.dto;


import com.nr.camunda.bpm.core.constant.ActivityStateEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 历史流程实例信息(只返回基础信息,不会做复杂查询的返回,保证性能)
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
public class ProcessInstanceDTO implements Serializable {

    private static final long serialVersionUID = -444652416458809484L;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 流程发起人id
     */
    private String applyUserId;
    /**
     * 表单类型
     */
    private String fromType;
    /**
     * 流程状态
     */
    private ActivityStateEnum state;
    /**
     * 租户id
     */
    private String tenantId;
}
