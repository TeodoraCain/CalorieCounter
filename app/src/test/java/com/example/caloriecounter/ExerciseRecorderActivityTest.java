package com.example.caloriecounter;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.example.caloriecounter.models.dao.DailyData;
import com.example.caloriecounter.models.dao.Workout;
import com.example.caloriecounter.models.dataHolders.DailyDataHolder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK}, manifest=Config.NONE)
public class ExerciseRecorderActivityTest {
    private ExerciseRecorderActivity exerciseRecorderActivity;

    @Before
    public void setUp() {
        exerciseRecorderActivity = new ExerciseRecorderActivity();
    }

    // severitate medie
    @Test
    public void testSaveToDailyData() {
        // setup
        DailyDataHolder dailyDataHolder = mock(DailyDataHolder.class);
        Workout workout = new Workout();

        mockStatic(DailyDataHolder.class);
        when(DailyDataHolder.getInstance()).thenReturn(dailyDataHolder);
        when(dailyDataHolder.getData()).thenReturn(null);

        DailyData resultDailyData = exerciseRecorderActivity.saveToDailyData(workout);

        // verifications
        List<Workout> updatedWorkouts = resultDailyData.getWorkouts();
        assertEquals(1, updatedWorkouts.size()); // check if one workout was added
        assertEquals(workout, updatedWorkouts.get(0)); // check if the added workout is correct
    }

}