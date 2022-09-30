package com.nr.camunda.bpm.core.constant;

/**
 * 流程全局变量
 */
public interface ActivityVariablesConstant {


	/**
	 * BPM 流程对应的流程业务KEY
	 */
	 String BPM_FORM_BUSINESS_KEY = "BPM_FORM_BUSINESS_KEY";
	/**
	 * BPM 流程对应的表单KEY
	 */
	String BPM_FORM_TYPE = "BPM_FORM_TYPE";
	/**
	 * BPM 发起人用户id
	 */
	String BPM_START_USER_ID = "BPM_START_USER_ID";
	/**
	 * BPM 发起租户
	 */
	String BPM_START_TENANT_ID = "BPM_START_TENANT_ID";
	/**
	 * BPM 用户信息实体对象
	 */
	String BPM_USER = "BPM_USER";
	/**
	 * 指定审批人变量
	 */
	String APPOINT_USER ="APPOINT_USER";
	/**
	 * 流程配置对象
	 */
	String BPM_CONFIG = "BPM_CONFIG";
}
