package com.nr.camunda.bpm.core.constant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 流程状态枚举（并不全，后续可补充）
 */
@AllArgsConstructor
@NoArgsConstructor
public enum ActivityStateEnum  {


	ACTIVE("ACTIVE", "审批中"),
	COMPLETED("COMPLETED", "已完成");

	/**
	 * 值
	 */
	private String value;
	/**
	 * 描述
	 */
	private String name;

	public static ActivityStateEnum getEnum(String value) {
		for (ActivityStateEnum t : ActivityStateEnum.values()) {
			if (t.getValue().equals(value)) {
				return t;
			}
		}
		return null;
	}

}
