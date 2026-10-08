package com.taskflow.taskservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class ProjectRequest {
    @NotBlank(message = "Project name is required")
    @Size(min = 2, max = 150)
    private String name;

    @Size(max = 2000)
    private String description;

    public ProjectRequest() {}
    public ProjectRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
