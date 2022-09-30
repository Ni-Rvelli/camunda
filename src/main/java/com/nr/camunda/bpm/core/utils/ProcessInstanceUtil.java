package com.nr.camunda.bpm.core.utils;

import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ActivityInstance;

/**
 * @Description: 流程实例工具类
 * @Author: nirui
 * @Date: 2020-05-27
 */
public class ProcessInstanceUtil {

    private static TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService() ;

    private static RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

    private static RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();


    /**
     * 获取对应的任务流程实例id
     * @param activityInstance
     * @param activityId
     * @return
     */
    public static String getInstanceIdForActivity(ActivityInstance activityInstance, String activityId) {
        ActivityInstance instance = getChildInstanceForActivity(activityInstance, activityId);
        if (instance != null) {
            return instance.getId();
        }
        return null;
    }

    /**
     * 获取子节点中对应的任务的流程实例
     * @param activityInstance
     * @param activityId
     * @return
     */
    public static ActivityInstance getChildInstanceForActivity(ActivityInstance activityInstance, String activityId) {
        if (activityId.equals(activityInstance.getActivityId())) {
            return activityInstance;
        }
        for (ActivityInstance childInstance : activityInstance.getChildActivityInstances()) {
            ActivityInstance instance = getChildInstanceForActivity(childInstance, activityId);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

}
