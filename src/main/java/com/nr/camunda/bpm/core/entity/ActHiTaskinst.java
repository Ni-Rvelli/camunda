package com.nr.camunda.bpm.core.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Description: 历史任务实体
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
@Entity
@Table(name = "act_hi_taskinst")
public class ActHiTaskinst {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    private String id;

    /**
     * 任务定义key
     */
    @Column(name = "TASK_DEF_KEY_")
    private String taskDefKey;

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
     * 流程实例根id
     */
    @Column(name = "ROOT_PROC_INST_ID_")
    private String rootProcInstId;

    /**
     * 流程实例id
     */
    @Column(name = "PROC_INST_ID_")
    private String procInstId;

    /**
     * 流程执行id
     */
    @Column(name = "EXECUTION_ID_")
    private String executionId;

    /**
     * 案例定义key
     */
    @Column(name = "CASE_DEF_KEY_")
    private String caseDefKey;

    /**
     * 案例定义id
     */
    @Column(name = "CASE_DEF_ID_")
    private String caseDefId;

    /**
     * 案例实例id
     */
    @Column(name = "CASE_INST_ID_")
    private String caseInstId;

    /**
     * 案例执行id
     */
    @Column(name = "CASE_EXECUTION_ID_")
    private String caseExecutionId;

    /**
     * 节点实例id
     */
    @Column(name = "ACT_INST_ID_")
    private String actInstId;

    /**
     * 名称
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 父任务id
     */
    @Column(name = "PARENT_TASK_ID_")
    private String parentTaskId;
    /**
     * 描述
     */
    @Column(name = "DESCRIPTION_")
    private String description;
    /**
     * 委托人id
     */
    @Column(name = "OWNER_")
    private String owner;
    /**
     * 办理人id
     */
    @Column(name = "ASSIGNEE_")
    private String assignee;
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
     * 耗时
     */
    @Column(name = "DURATION_")
    private Long duration;
    /**
     * 删除原因
     */
    @Column(name = "DELETE_REASON_")
    private String deleteReason;
    /**
     * 优先级
     */
    @Column(name = "PRIORITY_")
    private Long priority;
    /**
     * 超时时间
     */
    @Column(name = "DUE_DATE_")
    private Date dueDate;
    /**
     * 跟踪时间
     */
    @Column(name = "FOLLOW_UP_DATE_")
    private Date followUpDate;
    /**
     * 租户id
     */
    @Column(name = "TENANT_ID_")
    private String tenantId;
    /**
     * 移除时间
     */
    @Column(name = "REMOVAL_TIME_")
    private Date removalTime;
}
