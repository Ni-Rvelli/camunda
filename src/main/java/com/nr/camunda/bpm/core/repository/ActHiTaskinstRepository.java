package com.nr.camunda.bpm.core.repository;



import com.guoteng.kidlime.bpm.core.entity.ActHiTaskinst;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Description: 历史任务仓储类（用于bpm相关表查询）
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Repository
public interface ActHiTaskinstRepository extends JpaRepository<ActHiTaskinst,String> {

    @Query(value = "select * from act_hi_taskinst t where t.PROC_INST_ID_ = ?1 order by END_TIME_ desc", nativeQuery = true)
    List<ActHiTaskinst> findByProcInstId(@Param("procInstId") String procInstId);

}
