package com.nr.camunda.bpm.core.service.dto.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 运行中任务查询实体
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
@NoArgsConstructor
public class ProcessTaskRuQueryDTO implements Serializable {

    private static final long serialVersionUID = -4681198583803192288L;
    /**
     * 用户id（必传）
     */
    private String userId;
    /**
     * 发起人用户id
     */
    private String startUserId;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 任务发起时间开始
     */
    private Date taskStartTimeBegin;
    /**
     * 任务发起时间结束
     */
    private Date taskStartTimeEnd;
    /**
     * 审批类型（表单类型）
     */
    private String formType;
    /**
     * 租户id
     */
    private String tenantId;

    public ProcessTaskRuQueryDTO(String userId) {
        this.userId = userId;
    }
}
