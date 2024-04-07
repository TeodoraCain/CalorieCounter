package com.example.caloriecounter;


import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;

import com.example.caloriecounter.model.DAO.Food;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK}, manifest=Config.NONE)
public class NutritionDisplayTextWatcherChangeTest {
    @Mock
    private EditText etServingSize;

    @Mock
    private Food currentFood;

    @Mock
    private TextView tvCalories, tvCarbohydrate, tvProtein, tvTotalFat, tvSaturatedFat, tvFiber, tvIron, tvSugar,
            tvSodium, tvCalcium, tvMagnesium, tvVitaminA, tvVitaminB6, tvVitaminB12, tvVitaminC;

    private NutritionDisplayActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = new NutritionDisplayActivity();

        activity.etServingSize = etServingSize;
        activity.tvCalories = tvCalories;
        activity.tvCarbohydrate = tvCarbohydrate;
        activity.tvProtein = tvProtein;
        activity.tvTotalFat = tvTotalFat;
        activity.tvSaturatedFat = tvSaturatedFat;
        activity.tvFiber = tvFiber;
        activity.tvIron = tvIron;
        activity.tvSugar = tvSugar;
        activity.tvSodium = tvSodium;
        activity.tvCalcium = tvCalcium;
        activity.tvMagnesium = tvMagnesium;
        activity.tvVitaminA = tvVitaminA;
        activity.tvVitaminB6 = tvVitaminB6;
        activity.tvVitaminB12 = tvVitaminB12;
        activity.tvVitaminC = tvVitaminC;

        currentFood = new Food("Apple", 100.0, 52.0, 0.2, 0.0, 1.0, 54.0, 0.0, 0.0, 8.4, 0.0, 0.1, 5.0, 11.0, 0.2, 5.0, 107.0, 0.0, 13.8, 10.4, 2.4);
        activity.currentFood = currentFood;
    }

    @Test
    public void testSetListeners_TextWatcherUpdatesTextViews() {
        activity.setUpTextWatcherForServingSize();

        String newText = "200";
        Mockito.when(etServingSize.getText()).thenReturn(Editable.Factory.getInstance().newEditable(newText));

        double servingSize = Double.parseDouble(newText);
        double percentage = 1 + (servingSize - 100) / 100;
        activity.setModifiedTexts(percentage);

        Mockito.verify(tvCalories).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getCalories() * percentage));
        Mockito.verify(tvCarbohydrate).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getCarbohydrate() * percentage));
        Mockito.verify(tvProtein).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getProtein() * percentage));
        Mockito.verify(tvTotalFat).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getTotal_fat() * percentage));
        Mockito.verify(tvSaturatedFat).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getSaturated_fat() * percentage));
        Mockito.verify(tvFiber).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getFiber() * percentage));
        Mockito.verify(tvIron).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getIron() * percentage));
        Mockito.verify(tvSugar).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getSugars() * percentage));
        Mockito.verify(tvSodium).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getSodium() * percentage));
        Mockito.verify(tvCalcium).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getCalcium() * percentage));
        Mockito.verify(tvMagnesium).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getMagnesium() * percentage));
        Mockito.verify(tvVitaminA).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getVitamin_a() * percentage));
        Mockito.verify(tvVitaminB6).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getVitamin_b6() * percentage));
        Mockito.verify(tvVitaminB12).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getVitamin_b12() * percentage));
        Mockito.verify(tvVitaminC).setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getVitamin_c() * percentage));
    }

}
