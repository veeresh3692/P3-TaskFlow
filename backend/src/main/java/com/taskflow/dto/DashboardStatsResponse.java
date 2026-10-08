package com.taskflow.dto;

import java.util.Map;

public class DashboardStatsResponse {
    private long totalProjects;
    private long activeSprints;
    private long totalTasks;
    private long completedTasks;
    private long highPriorityTasks;
    private Map<String, Long> tasksByStatus;
    private Map<String, Long> tasksByPriority;

    public DashboardStatsResponse() {}

    public DashboardStatsResponse(long totalProjects, long activeSprints, long totalTasks,
                                  long completedTasks, long highPriorityTasks,
                                  Map<String, Long> tasksByStatus, Map<String, Long> tasksByPriority) {
        this.totalProjects = totalProjects;
        this.activeSprints = activeSprints;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.highPriorityTasks = highPriorityTasks;
        this.tasksByStatus = tasksByStatus;
        this.tasksByPriority = tasksByPriority;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getActiveSprints() {
        return activeSprints;
    }

    public void setActiveSprints(long activeSprints) {
        this.activeSprints = activeSprints;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getHighPriorityTasks() {
        return highPriorityTasks;
    }

    public void setHighPriorityTasks(long highPriorityTasks) {
        this.highPriorityTasks = highPriorityTasks;
    }

    public Map<String, Long> getTasksByStatus() {
        return tasksByStatus;
    }

    public void setTasksByStatus(Map<String, Long> tasksByStatus) {
        this.tasksByStatus = tasksByStatus;
    }

    public Map<String, Long> getTasksByPriority() {
        return tasksByPriority;
    }

    public void setTasksByPriority(Map<String, Long> tasksByPriority) {
        this.tasksByPriority = tasksByPriority;
    }
}
