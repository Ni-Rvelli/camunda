package com.nr.camunda.bpm.core.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 流程实例历史记录
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
@Entity
@Table(name = "act_hi_procinst")
public class ActHiProcinst implements Serializable {

    private static final long serialVersionUID = -5503308830052735051L;
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    private String id;
    /**
     * 流程实例id
     */
    @Column(name = "PROC_INST_ID_")
    private String procInstId;
    /**
     * 业务key
     */
    @Column(name = "BUSINESS_KEY_")
    private String businessKey;
    /**
     * 流程定义key
     */
    @Column(name = "PROC_DEF_KEY_")
    private String procDefKey;
    /**
     * 流程定义id
     */
    @Column(name = "PROC_DEF_ID_")
    private String procDefId;
    /**
     * 开始时间
     */
    @Column(name = "START_TIME_")
    private Date startTime;
    /**
     * 结束时间
     */
    @Column(name = "END_TIME_")
    private Date endTime;
    /**
     * 移除时间
     */
    @Column(name = "REMOVAL_TIME_")
    private Date removalTime;
    /**
     * 耗时
     */
    @Column(name = "DURATION_")
    private Long duration;
    /**
     * 发起人id
     */
    @Column(name = "START_USER_ID_")
    private String startUserId;
    /**
     * 发起节点id
     */
    @Column(name = "START_ACT_ID_")
    private String startActId;
    /**
     * 结束节点id
     */
    @Column(name = "END_ACT_ID_")
    private String endActId;
    /**
     * 父流程实例id
     */
    @Column(name = "SUPER_PROCESS_INSTANCE_ID_")
    private String superProcessInstanceId;
    /**
     * 流程实例根id
     */
    @Column(name = "ROOT_PROC_INST_ID_")
    private String rootProcInstId;
    /**
     * 父案例实例id
     */
    @Column(name = "SUPER_CASE_INSTANCE_ID_")
    private String superCaseInstanceId;
    /**
     * 案例实例id
     */
    @Column(name = "CASE_INST_ID_")
    private String caseInstId;
    /**
     * 删除原因
     */
    @Column(name = "DELETE_REASON_")
    private String deleteReason;
    /**
     * 租户id
     */
    @Column(name = "TENANT_ID_")
    private String tenantId;
    /**
     * 状态
     */
    @Column(name = "STATE_")
    private String state;
}