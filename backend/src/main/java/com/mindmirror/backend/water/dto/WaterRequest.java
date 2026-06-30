package com.mindmirror.backend.water.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WaterRequest {

    @NotNull
    private LocalDate entryDate;

    @NotNull
    @Min(0)
    private Integer glasses;

    @Min(1)
    private Integer goalGlasses;

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getGlasses() {
        return glasses;
    }

    public void setGlasses(Integer glasses) {
        this.glasses = glasses;
    }

    public Integer getGoalGlasses() {
        return goalGlasses;
    }

    public void setGoalGlasses(Integer goalGlasses) {
        this.goalGlasses = goalGlasses;
    }
}
