package com.nr.camunda.bpm.core.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 历史流程实例信息
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Data
public class ProcessInstanceListDTO extends ProcessInstanceDTO implements Serializable {

    private static final long serialVersionUID = -444652416458809484L;

    /**
     * 最后审批时间
     */
    private Date lastOpTime;
    /**
     * 当前审批人名称
     */
    private List<String> currentOpUserIds;
}
