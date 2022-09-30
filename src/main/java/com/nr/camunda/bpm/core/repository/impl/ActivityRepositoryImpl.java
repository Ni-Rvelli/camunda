package com.nr.camunda.bpm.core.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import com.guoteng.kidlime.bpm.core.constant.ActivityVariablesConstant;
import com.guoteng.kidlime.bpm.core.entity.ActHiProcinst;
import com.guoteng.kidlime.bpm.core.entity.ActHiTaskinst;
import com.guoteng.kidlime.bpm.core.entity.ActRuTask;
import com.guoteng.kidlime.bpm.core.service.dto.query.ProcessInstanceQueryDTO;
import com.guoteng.kidlime.bpm.core.service.dto.query.ProcessTaskHistoryQueryDTO;
import com.guoteng.kidlime.bpm.core.service.dto.query.ProcessTaskRuQueryDTO;
import com.guoteng.kidlime.bpm.core.utils.SqlInjectionUtil;
import com.guoteng.mango.core.util.StringUtils;
import com.guoteng.mango.repository.query.QueryPagingResult;
import com.guoteng.mango.rest.support.Pager;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 流程仓储类（用于bpm相关表查询）
 * @Author: nirui
 * @Date: 2020-05-27
 */
@Repository
public class ActivityRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 查询代办任务
     *
     * @param queryDTO
     * @param pager
     * @return
     */
    public QueryPagingResult findRuTask(ProcessTaskRuQueryDTO queryDTO, Pager pager) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer selectSql = new StringBuffer("SELECT DISTINCT RES.* ");
        StringBuffer countSql = new StringBuffer(" SELECT COUNT(*)  ");
        StringBuffer commonSql = new StringBuffer(" FROM ACT_RU_TASK RES ");
        commonSql.append(" LEFT JOIN ACT_RU_IDENTITYLINK ARI ON ARI.TASK_ID_ = RES.ID_ ");
        commonSql.append(" WHERE 1=1 ");
        if (StringUtils.isNotBlank(queryDTO.getUserId())) {
            commonSql.append(" AND ( ARI.USER_ID_ = :userId OR ARI.GROUP_ID_ IN ( SELECT t.ROLE_ID FROM SYS_USER_ROLE t WHERE t.USER_ID = :userId )) ");
            map.put("userId", queryDTO.getUserId());
        }
        if (StringUtils.isNotBlank(queryDTO.getProcessInstanceId())) {
            commonSql.append(" AND RES.PROC_INST_ID_ = :processInstanceId");
            map.put("processInstanceId", queryDTO.getProcessInstanceId());
        }
        if (StringUtils.isNotBlank(queryDTO.getStartUserId())) {
            commonSql.append(" AND EXIST (SELECT ARV.PROC_INST_ID_ FROM  ACT_RU_VARIABLE ARV WHERE RES.PROC_INST_ID_ = ARV.PROC_INST_ID_ AND ARV.NAME_ = " + ActivityVariablesConstant.BPM_START_USER_ID + " AND TEXT_ = :startUserId ) ");
            map.put("startUserId", queryDTO.getStartUserId());
        }
        if (StringUtils.isNotBlank(queryDTO.getFormType())) {
            commonSql.append(" AND EXIST (SELECT ARV.PROC_INST_ID_ FROM  ACT_RU_VARIABLE ARV WHERE RES.PROC_INST_ID_ = ARV.PROC_INST_ID_ AND ARV.NAME_ = " + ActivityVariablesConstant.BPM_FORM_TYPE + " AND TEXT_ = :formType ) ");
            map.put("formType", queryDTO.getFormType());
        }
        if (StringUtils.isNotBlank(queryDTO.getTenantId())) {
            commonSql.append(" AND EXIST (SELECT ARV.PROC_INST_ID_ FROM  ACT_RU_VARIABLE ARV WHERE RES.PROC_INST_ID_ = ARV.PROC_INST_ID_ AND ARV.NAME_ = " + ActivityVariablesConstant.BPM_START_TENANT_ID + " AND TEXT_ = :tenantId ) ");
            map.put("tenantId", queryDTO.getTenantId());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getTaskStartTimeBegin())) {
            commonSql.append(" AND RES.CREATE_TIME_ >= :taskStartTimeBegin");
            map.put("taskStartTimeBegin", queryDTO.getTaskStartTimeBegin());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getTaskStartTimeEnd())) {
            commonSql.append(" AND RES.CREATE_TIME_ <= :taskStartTimeEnd");
            map.put("taskStartTimeEnd", queryDTO.getTaskStartTimeEnd());
        }
        Query selectQuery = entityManager.createNativeQuery(selectSql.append(commonSql).toString(), ActRuTask.class);
        for (String key : map.keySet()) {
            selectQuery.setParameter(key, map.get(key));
        }
        if (pager != null) {
            selectQuery.setFirstResult(pager.getOffset());
            selectQuery.setMaxResults(pager.getLimit());
        }
        Query countQuery = entityManager.createNativeQuery(countSql.append(commonSql).toString());
        for (String key : map.keySet()) {
            countQuery.setParameter(key, map.get(key));
        }
        return new QueryPagingResult(((BigInteger) countQuery.getSingleResult()).longValue(), selectQuery.getResultList());
    }

    /**
     * 查询已完成任务
     *
     * @param queryDTO
     * @param pager
     * @return
     */
    public QueryPagingResult findHistoricTask(ProcessTaskHistoryQueryDTO queryDTO, Pager pager) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer selectSql = new StringBuffer("SELECT DISTINCT AHT.* ");
        StringBuffer countSql = new StringBuffer(" SELECT COUNT(*)  ");
        StringBuffer commonSql = new StringBuffer(" FROM ACT_HI_TASKINST AHT ");
        commonSql.append(" LEFT JOIN ACT_HI_PROCINST AHP ON AHT.PROC_INST_ID_ = AHP.PROC_INST_ID_ ");
        commonSql.append(" WHERE 1=1 ");
        commonSql.append(" AND AHT.END_TIME_ IS NOT NULL ");
        if (StringUtils.isNotBlank(queryDTO.getUserId())) {
            commonSql.append(" AND AHT.ASSIGNEE_ = :userId ");
            map.put("userId", queryDTO.getUserId());
        }
        if (StringUtils.isNotBlank(queryDTO.getProcessInstanceId())) {
            commonSql.append(" AND AHT.PROC_INST_ID_ = :processInstanceId");
            map.put("processInstanceId", queryDTO.getProcessInstanceId());
        }
        if (StringUtils.isNotBlank(queryDTO.getStartUserId())) {
            commonSql.append(" AND AHP.START_USER_ID_= :startUserId ");
            map.put("startUserId", queryDTO.getStartUserId());
        }
        if (StringUtils.isNotBlank(queryDTO.getFormType())) {
            commonSql.append(" AND EXIST (SELECT AHV.PROC_INST_ID_ FROM  ACT_HI_VARINST AHV WHERE AHT.PROC_INST_ID_ = AHV.PROC_INST_ID_ AND AHV.NAME_ = " + ActivityVariablesConstant.BPM_FORM_TYPE + " AND TEXT_ = :formType ) ");
            map.put("formType", queryDTO.getFormType());
        }
        if (StringUtils.isNotBlank(queryDTO.getTenantId())) {
            commonSql.append(" AND EXIST (SELECT AHV.PROC_INST_ID_ FROM  ACT_HI_VARINST AHV WHERE AHT.PROC_INST_ID_ = AHV.PROC_INST_ID_ AND AHV.NAME_ = " + ActivityVariablesConstant.BPM_START_TENANT_ID + " AND TEXT_ = :tenantId ) ");
            map.put("tenantId", queryDTO.getTenantId());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getTaskStartTimeBegin())) {
            commonSql.append(" AND AHT.CREATE_TIME_ >= :taskStartTimeBegin");
            map.put("taskStartTimeBegin", queryDTO.getTaskStartTimeBegin());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getTaskStartTimeEnd())) {
            commonSql.append(" AND AHT.CREATE_TIME_ <= :taskStartTimeEnd");
            map.put("taskStartTimeEnd", queryDTO.getTaskStartTimeEnd());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getApprovalTimeBegin())) {
            commonSql.append(" AND AHT.END_TIME_ >= :approvalTimeBegin");
            map.put("approvalTimeBegin", queryDTO.getApprovalTimeBegin());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getApprovalTimeEnd())) {
            commonSql.append(" AND AHT.END_TIME_ <= :approvalTimeEnd");
            map.put("approvalTimeEnd", queryDTO.getApprovalTimeEnd());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getState())) {
            commonSql.append(" AND AHP.STATE_ = :state");
            map.put("state", queryDTO.getState().getValue());
        }
        Query selectQuery = entityManager.createNativeQuery(selectSql.append(commonSql).toString(), ActHiTaskinst.class);
        for (String key : map.keySet()) {
            selectQuery.setParameter(key, map.get(key));
        }
        if (pager != null) {
            selectQuery.setFirstResult(pager.getOffset());
            selectQuery.setMaxResults(pager.getLimit());
        }
        Query countQuery = entityManager.createNativeQuery(countSql.append(commonSql).toString());
        for (String key : map.keySet()) {
            countQuery.setParameter(key, map.get(key));
        }
        return new QueryPagingResult(((BigInteger) countQuery.getSingleResult()).longValue(), selectQuery.getResultList());
    }


    /**
     * 查询流程实例列表
     *
     * @param queryDTO
     * @param pager
     * @return
     */
    public QueryPagingResult findProcessInstance(ProcessInstanceQueryDTO queryDTO, Pager pager) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer selectSql = new StringBuffer("SELECT AHP.* ");
        StringBuffer countSql = new StringBuffer(" SELECT COUNT(*)  ");
        StringBuffer commonSql = new StringBuffer(" FROM ACT_HI_PROCINST AHP ");
        commonSql.append(" WHERE 1=1 ");
        if (StringUtils.isNotBlank(queryDTO.getProcessInstanceId())) {
            commonSql.append(" AND AHP.PROC_INST_ID_ = :processInstanceId");
            map.put("processInstanceId", queryDTO.getProcessInstanceId());
        }
        if (StringUtils.isNotBlank(queryDTO.getStartUserId())) {
            commonSql.append(" AND AHP.START_USER_ID_= :startUserId ");
            map.put("startUserId", queryDTO.getStartUserId());
        }
        if (StringUtils.isNotBlank(queryDTO.getTenantId())) {
            commonSql.append(" AND EXIST (SELECT AHV.PROC_INST_ID_ FROM  ACT_HI_VARINST AHV WHERE AHP.PROC_INST_ID_ = AHV.PROC_INST_ID_ AND AHV.NAME_ = " + ActivityVariablesConstant.BPM_START_TENANT_ID + " AND TEXT_ = :tenantId ) ");
            map.put("tenantId", queryDTO.getTenantId());
        }
        if (StringUtils.isNotBlank(queryDTO.getFormType())) {
            commonSql.append(" AND EXIST (SELECT AHV.PROC_INST_ID_ FROM  ACT_HI_VARINST AHV WHERE AHP.PROC_INST_ID_ = AHV.PROC_INST_ID_ AND AHV.NAME_ = " + ActivityVariablesConstant.BPM_FORM_TYPE + " AND TEXT_ = :formType ) ");
            map.put("formType", queryDTO.getFormType());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getStartTimeBegin())) {
            commonSql.append(" AND AHP.START_TIME_ >= :startTimeBegin");
            map.put("startTimeBegin", queryDTO.getStartTimeBegin());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getStartTimeEnd())) {
            commonSql.append(" AND AHP.START_TIME_ <= :startTimeEnd");
            map.put("startTimeEnd", queryDTO.getStartTimeEnd());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getLastOpTimeBegin())) {
            commonSql.append(" AND (SELECT MAX(AHT.END_TIME_) from ACT_HI_TASKINST AHT WHERE AHT.PROC_INST_ID_ =AHP.PROC_INST_ID_ AND AHT.END_TIME_ IS NOT NULL GROUP BY AHT.PROC_INST_ID_) >= :lastOpTimeBegin");
            map.put("lastOpTimeBegin", queryDTO.getLastOpTimeBegin());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getLastOpTimeEnd())) {
            commonSql.append(" AND (SELECT MAX(AHT.END_TIME_) from ACT_HI_TASKINST AHT WHERE AHT.PROC_INST_ID_ =AHP.PROC_INST_ID_ AND AHT.END_TIME_ IS NOT NULL GROUP BY AHT.PROC_INST_ID_) <= :lastOpTimeEnd");
            map.put("lastOpTimeEnd", queryDTO.getLastOpTimeEnd());
        }
        if (ObjectUtil.isNotEmpty(queryDTO.getState())) {
            commonSql.append(" AND AHP.STATE_ = :state");
            map.put("state", queryDTO.getState().getValue());
        }
        Query selectQuery = entityManager.createNativeQuery(selectSql.append(commonSql).toString(), ActHiProcinst.class);
        for (String key : map.keySet()) {
            selectQuery.setParameter(key, map.get(key));
        }
        if (pager != null) {
            selectQuery.setFirstResult(pager.getOffset());
            selectQuery.setMaxResults(pager.getLimit());
        }
        Query countQuery = entityManager.createNativeQuery(countSql.append(commonSql).toString());
        for (String key : map.keySet()) {
            countQuery.setParameter(key, map.get(key));
        }
        return new QueryPagingResult(((BigInteger) countQuery.getSingleResult()).longValue(), selectQuery.getResultList());
    }

    /**
     * 动态查询sql结果集
     *
     * @param table
     * @param id
     * @return
     */
    public List<Map<String, Object>> getListMap(String table, String id) {
        table = SqlInjectionUtil.escapeExprSpecialWord(table);
        id = SqlInjectionUtil.escapeExprSpecialWord(id);
        String selectSql = "SELECT * from " + table + " where id = " + id;
        Query nativeQuery = entityManager.createNativeQuery(selectSql.toString());
        nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List resultList = nativeQuery.getResultList();
        return resultList;
    }
}
