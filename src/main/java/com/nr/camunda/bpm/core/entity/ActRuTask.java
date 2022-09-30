package com.nr.camunda.bpm.core.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Description:流程运行时任务表
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
@Entity
@Table(name = "act_ru_task")
public class ActRuTask {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    private String id;
    /**
     * 版本
     */
    @Column(name = "REV_")
    private Long rev;
    /**
     * 流程执行id
     */
    @Column(name = "EXECUTION_ID_")
    private String executionId;
    /**
     * 流程实例id
     */
    @Column(name = "PROC_INST_ID_")
    private String procInstId;
    /**
     * 流程定义id
     */
    @Column(name = "PROC_DEF_ID_")
    private String procDefId;

    /**
     * 案例执行id
     */
    @Column(name = "CASE_EXECUTION_ID_")
    private String caseExecutionId;
    /**
     * 案例实例id
     */
    @Column(name = "CASE_INST_ID_")
    private String caseInstId;
    /**
     * 案例定义id
     */
    @Column(name = "CASE_DEF_ID_")
    private String caseDefId;
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
     * 任务定义key
     */
    @Column(name = "TASK_DEF_KEY_")
    private String taskDefKey;
    /**
     * 委托人
     */
    @Column(name = "OWNER_")
    private String owner;
    /**
     * 办理人
     */
    @Column(name = "ASSIGNEE_")
    private String assignee;
    /**
     * 委托状态
     */
    @Column(name = "DELEGATION_")
    private String delegation;
    /**
     * 优先级
     */
    @Column(name = "PRIORITY_")
    private Long priority;
    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME_")
    private Date createTime;
    /**
     * 截止时间
     */
    @Column(name = "DUE_DATE_")
    private Date dueDate;
    /**
     * 跟踪时间
     */
    @Column(name = "FOLLOW_UP_DATE_")
    private Date followUpDate;
    /**
     * 挂起状态
     */
    @Column(name = "SUSPENSION_STATE_")
    private Long suspensionstate;
    /**
     * 租户id
     */
    @Column(name = "TENANT_ID_")
    private String tenantId;

}
