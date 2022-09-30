package com.nr.camunda.bpm.core.mapper;


import com.nr.camunda.bpm.core.entity.ActHiTaskinst;
import com.nr.camunda.bpm.core.service.dto.ProcessTaskHistoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

/**
 * @Description: 历史任务对象转换类
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Mapper(imports = {Arrays.class})
public interface ProcessHiTaskMapper {


    ProcessHiTaskMapper INSTANCE = Mappers.getMapper(ProcessHiTaskMapper.class);

    /**
     * ActHiTaskinst 2 TaskHistoryDTO
     * @param actHiTaskinst
     * @return
     */
    @Mappings({
            @Mapping(target = "processInstanceId", source = "actHiTaskinst.procInstId"),
            @Mapping(target = "opUserIds", expression = "java(Arrays.asList(actHiTaskinst.getAssignee()))"),
            @Mapping(target = "taskId", source = "actHiTaskinst.id")
    })
    ProcessTaskHistoryDTO toHistoryTask(ActHiTaskinst actHiTaskinst);
}
