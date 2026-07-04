package com.mindmirror.backend.routine.dto;

import jakarta.validation.constraints.Size;

public class UpdateRoutineTaskRequest {

    @Size(max = 255)
    private String title;

    @Size(max = 80)
    private String category;

    private Integer sortOrder;
    private Boolean active;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
