package com.example.caloriecounter;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;

import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dao.Recipe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK}, manifest=Config.NONE)
public class NutritionDisplayActivityTest {
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
        MockitoAnnotations.openMocks(this);
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

    //severitate medie
    @Test
    public void testUpdateTextViewsFromTextWatcher() {
        activity.setUpTextWatcherForServingSize();

        String newText = "200";
        when(etServingSize.getText()).thenReturn(Editable.Factory.getInstance().newEditable(newText));

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

    //severitate medie
    @Test
    public void testCreateFoodItemFromUI() {
        // Mock EditText and TextView values
        when(etServingSize.getText()).thenReturn(Editable.Factory.getInstance().newEditable("1.0"));
        when(tvCalories.getText()).thenReturn("100.0");
        when(tvCarbohydrate.getText()).thenReturn("10.0");
        when(tvProtein.getText()).thenReturn("15.0");
        when(tvTotalFat.getText()).thenReturn("5.0");
        when(tvSaturatedFat.getText()).thenReturn("2.0");
        when(tvFiber.getText()).thenReturn("3.0");
        when(tvIron.getText()).thenReturn("1.5");
        when(tvSugar.getText()).thenReturn("20.0");
        when(tvSodium.getText()).thenReturn("25.0");
        when(tvCalcium.getText()).thenReturn("30.0");
        when(tvMagnesium.getText()).thenReturn("35.0");
        when(tvVitaminA.getText()).thenReturn("40.0");
        when(tvVitaminB6.getText()).thenReturn("45.0");
        when(tvVitaminB12.getText()).thenReturn("50.0");
        when(tvVitaminC.getText()).thenReturn("55.0");

        // Call the method to test
        Food food = activity.createFoodItemFromUI();

        // Verify the expected values
        assertEquals("Apple", food.getName());
        assertEquals(1.0, food.getServing_size(), 0.001);
        assertEquals(100.0, food.getCalories(), 0.001);
        assertEquals(10.0, food.getCarbohydrate(), 0.001);
        assertEquals(15.0, food.getProtein(), 0.001);
        assertEquals(5.0, food.getTotal_fat(), 0.001);
        assertEquals(2.0, food.getSaturated_fat(), 0.001);
        assertEquals(3.0, food.getFiber(), 0.001);
        assertEquals(1.5, food.getIron(), 0.001);
        assertEquals(20.0, food.getSugars(), 0.001);
        assertEquals(25.0, food.getSodium(), 0.001);
        assertEquals(30.0, food.getCalcium(), 0.001);
        assertEquals(35.0, food.getMagnesium(), 0.001);
        assertEquals(40.0, food.getVitamin_a(), 0.001);
        assertEquals(45.0, food.getVitamin_b6(), 0.001);
        assertEquals(50.0, food.getVitamin_b12(), 0.001);
        assertEquals(55.0, food.getVitamin_c(), 0.001);
    }

    //severitate scazuta
    @Test
    public void testCreateRecipeFromFood() {
        Food food = new Food("Apple", 100.0, 52.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        List<Food> foods = new ArrayList<>();
        foods.add(food);

        Recipe recipe = activity.createRecipeFromFood(food, foods);

        assertEquals("Apple", recipe.getName());
        assertEquals(100.0, recipe.getServing_size(), 0.001);
        assertEquals(52.0, recipe.getCalories(), 0.001);
    }


}
