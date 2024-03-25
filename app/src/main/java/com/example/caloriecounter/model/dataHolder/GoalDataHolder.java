package com.example.caloriecounter.model.dataHolder;

import com.example.caloriecounter.model.DAO.GoalData;

public class GoalDataHolder {
    private GoalData goalData;

    public GoalData getData() {
        return goalData;
    }

    public void setData(GoalData goalData) {
        this.goalData = goalData;
    }

    private static final GoalDataHolder holder = new GoalDataHolder();

    public static GoalDataHolder getInstance() {
        return holder;
    }
}
