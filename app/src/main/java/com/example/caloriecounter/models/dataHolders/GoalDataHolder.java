package com.example.caloriecounter.models.dataHolders;

import com.example.caloriecounter.models.dao.GoalData;

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
