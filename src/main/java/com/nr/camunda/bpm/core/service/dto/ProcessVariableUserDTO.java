package com.nr.camunda.bpm.core.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 用于流程中执行参数的用户实体
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
public class ProcessVariableUserDTO implements Serializable {

    private static final long serialVersionUID = -8486784442431030752L;
    /**
     * id
     */
    private String id;
    /**
     * 角色集合，多个逗号分隔
     */
    private String roleIds;
    /**
     * 部门id，多个逗号分隔
     */
    private String departmentIds;
    /**
     * 直属上级，多个逗号分隔
     */
    private String directorLeaderId;
    /**
     * 部门负责人，多个逗号分隔
     */
    private String departmentLeaderIds;
    /**
     * 部门分管领导，多个逗号分隔
     */
    private String departmentChargeIds;

}
