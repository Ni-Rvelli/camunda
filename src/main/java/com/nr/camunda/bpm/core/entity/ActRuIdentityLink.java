package com.nr.camunda.bpm.core.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "act_ru_identityLink")
public class ActRuIdentityLink implements Serializable {

    private static final long serialVersionUID = -6015729171874322834L;

    @Id
    @Column(name = "ID_")
    private String id;

    /**
     * 版本
     */
    @Column(name = "REV_")
    private String rev;
    /**
     * 用户组
     */
    @Column(name = "GROUP_ID_")
    private String groupId;
    /**
     * 类型
     */
    @Column(name = "TYPE_")
    private String type;
    /**
     * 用户id
     */
    @Column(name = "USER_ID_")
    private String userId;
    /**
     * 任务id
     */
    @Column(name = "TASK_ID_")
    private String taskId;
    /**
     * 流程定义id
     */
    @Column(name = "PROC_DEF_ID_")
    private String procDefId;
    /**
     * 租户id
     */
    @Column(name = "TENANT_ID_")
    private String tenantId;
}
