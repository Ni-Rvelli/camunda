package com.nr.camunda.bpm.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * sql注入处理工具类
 * 
 * @author zhoujf
 */
@Slf4j
public class SqlInjectionUtil {

	/**
	 * sql特殊字符
	 */
	private static String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|", "'"};




	/**
	 * 特殊字符转义，防止sql注入
	 * @param keyword
	 * @return
	 */
	public static String escapeExprSpecialWord(String keyword) {
		if (StringUtils.isBlank(keyword)) {
			return keyword;
		}
		for (String key : fbsArr) {
			if (keyword.contains(key)) {
				keyword = keyword.replace(key, "\\" + key);
			}
		}
		return keyword;
	}

}
