package com.example.caloriecounter.models.dataHolders;

import com.example.caloriecounter.models.dao.DailyData;

public class DailyDataHolder {
    private DailyData dailyData;

    public DailyData getData() {
        return dailyData;
    }

    public void setData(DailyData dailyData) {
        this.dailyData = dailyData;
    }

    private static final DailyDataHolder holder = new DailyDataHolder();

    public static DailyDataHolder getInstance() {
        return holder;
    }
}
