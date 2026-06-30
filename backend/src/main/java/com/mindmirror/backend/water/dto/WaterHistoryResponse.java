package com.mindmirror.backend.water.dto;

import java.time.LocalDate;

public class WaterHistoryResponse {

    private LocalDate entryDate;
    private Integer glasses;
    private Integer goalGlasses;
    private boolean goalMet;

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

    public boolean isGoalMet() {
        return goalMet;
    }

    public void setGoalMet(boolean goalMet) {
        this.goalMet = goalMet;
    }
}
