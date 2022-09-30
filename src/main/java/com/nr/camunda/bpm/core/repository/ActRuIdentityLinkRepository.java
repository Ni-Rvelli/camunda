package com.nr.camunda.bpm.core.repository;



import com.guoteng.kidlime.bpm.core.entity.ActRuIdentityLink;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Description: 任务关联人员仓储类
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Repository
public interface ActRuIdentityLinkRepository extends JpaRepository<ActRuIdentityLink,String> {

    @Query(value = "select * from  ACT_RU_IDENTITYLINK where task_id_ = ?1", nativeQuery = true)
    List<ActRuIdentityLink> findByTaskId(@Param("taskId") String taskId);

}
