package com.example.caloriecounter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.widget.TextView;

import com.example.caloriecounter.models.dao.GoalData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK}, manifest = Config.NONE)
public class GoalsActivityTest {

    private GoalsActivity goalsActivity;
    @Mock
    private TextView mockedCalorieGoal;
    @Mock
    private TextView mockedExerciseGoal;
    @Mock
    private TextView mockedWeightGoal;
    @Mock
    private TextView mockedWaterGoal;
    @Mock
    private TextView mockedStepGoal;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        goalsActivity = new GoalsActivity();

        goalsActivity.tvCalorieGoal = mockedCalorieGoal;
        goalsActivity.tvExerciseGoal = mockedExerciseGoal;
        goalsActivity.tvWeightGoal = mockedWeightGoal;
        goalsActivity.tvWaterGoal = mockedWaterGoal;
        goalsActivity.tvStepGoal = mockedStepGoal;
    }

    // severitate scazuta
    @Test
    public void testGetGoalDataFromView() {
        GoalData goalData = new GoalData();
        goalsActivity.goalData = goalData;

        when(mockedCalorieGoal.getText()).thenReturn("2000 kcal");
        when(mockedExerciseGoal.getText()).thenReturn("30 min");
        when(mockedWeightGoal.getText()).thenReturn("70 kg");
        when(mockedWaterGoal.getText()).thenReturn("2000 ml");
        when(mockedStepGoal.getText()).thenReturn("10000 steps");

        goalsActivity.getGoalDataFromView();

        assertEquals("2000", goalData.getCalorieGoal());
        assertEquals("30", goalData.getExerciseTimeGoal());
        assertEquals("70", goalData.getWeightGoal());
        assertEquals("2000", goalData.getWaterIntakeGoal());
        assertEquals("10000", goalData.getStepGoal());
    }

}
