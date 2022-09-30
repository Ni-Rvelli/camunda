package com.nr.camunda.bpm.core.mapper;


import com.nr.camunda.bpm.core.entity.ActRuTask;
import com.nr.camunda.bpm.core.service.dto.ProcessTaskRuDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 运行任务对象转换类
 *
 * @author bifeng.liu
 * @date 2022/07/03
 */
@Mapper
public interface ProcessRuTaskMapper {

    ProcessRuTaskMapper INSTANCE = Mappers.getMapper(ProcessRuTaskMapper.class);

    /**
     * ActRuTask 2 TaskTodoDTO
     * @param actRuTask
     * @return
     */
    @Mappings({
            @Mapping(target = "processInstanceId", source = "actRuTask.procInstId"),
            @Mapping(target = "startTime", source = "actRuTask.createTime"),
            @Mapping(target = "taskId", source = "actRuTask.id")
    })
    ProcessTaskRuDTO toRuTask(ActRuTask actRuTask);
}
