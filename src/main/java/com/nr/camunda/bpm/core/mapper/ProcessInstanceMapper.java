package com.nr.camunda.bpm.core.mapper;


import com.nr.camunda.bpm.core.entity.ActHiProcinst;
import com.nr.camunda.bpm.core.service.dto.ProcessInstanceDTO;
import com.nr.camunda.bpm.core.service.dto.ProcessInstanceDetailsDTO;
import com.nr.camunda.bpm.core.service.dto.ProcessInstanceListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @Description: 历史流程实例转换类
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Mapper
public interface ProcessInstanceMapper {

    ProcessInstanceMapper INSTANCE = Mappers.getMapper(ProcessInstanceMapper.class);

    /**
     * actHiProcinst 2 ProcessInstanceDTO
     * @param actHiProcinst
     * @return
     */
    @Mappings({
            @Mapping(target = "processInstanceId", source = "actHiProcinst.procInstId"),
            @Mapping(target = "applyUserId", source = "actHiProcinst.startUserId"),
    })
    ProcessInstanceListDTO toProcessInstanceList(ActHiProcinst actHiProcinst);

    /**
     * processInstanceDTO 2 ProcessInstanceDetailsDTO
     * @param processInstanceDTO
     * @return
     */
    ProcessInstanceDetailsDTO toDetails(ProcessInstanceDTO processInstanceDTO);
}
