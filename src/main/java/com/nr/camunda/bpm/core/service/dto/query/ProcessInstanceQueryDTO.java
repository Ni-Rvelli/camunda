package com.nr.camunda.bpm.core.service.dto.query;

import com.guoteng.kidlime.bpm.core.constant.ActivityStateEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 历史流程实例查询信息
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
public class ProcessInstanceQueryDTO implements Serializable {


    private static final long serialVersionUID = -6621506026323126090L;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 发起时间开始
     */
    private Date startTimeBegin;
    /**
     * 发起时间结束
     */
    private Date startTimeEnd;
    /**
     * 流程发起人id
     */
    private String startUserId;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 流程状态
     */
    private ActivityStateEnum state;
    /**
     * 最后审批时间开始
     */
    private Date lastOpTimeBegin;
    /**
     * 最后审批时间结束
     */
    private Date lastOpTimeEnd;
    /**
     * 审批类型（表单类型）
     */
    private String formType;
}
