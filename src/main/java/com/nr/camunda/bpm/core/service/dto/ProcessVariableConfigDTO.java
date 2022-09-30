package com.nr.camunda.bpm.core.service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 流程配置对象
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
@AllArgsConstructor
public class ProcessVariableConfigDTO implements Serializable {

    private static final long serialVersionUID = 1546299446742204663L;
    /**
     * 流程中多次出现，自动审批
     */
    private Boolean multipleApproval;
    /**
     * 流程中连续出现，自动审批
     */
    private Boolean successiveApproval;
    /**
     * 审批人和发起人是同一个人，自动通过
     */
    private Boolean sameApplyApproval;
    /**
     * 无需审批，发起即归档
     */
    private Boolean autoApproval;

    public ProcessVariableConfigDTO() {
        this.multipleApproval = false;
        this.successiveApproval = false;
        this.sameApplyApproval = false;
        this.autoApproval = false;
    }
}
