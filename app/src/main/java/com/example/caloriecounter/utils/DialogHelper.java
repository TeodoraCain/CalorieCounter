package com.example.caloriecounter.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.caloriecounter.R;
import com.example.caloriecounter.models.dataModel.Meal;

public class DialogHelper {

    /**
     * Shows a custom bottom sheet dialog with meal options (e.g., breakfast, lunch, dinner, snack).
     * This dialog allows the user to select a meal type from predefined options.
     *
     * @param context The context in which the dialog should be shown (usually an Activity or Fragment).
     * @param mealClickListener The listener for handling meal option clicks.
     */
    public static void showBottomSheetDialog(Context context, MealClickListener mealClickListener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cutom_layout_bottom_sheet);

//        LinearLayout addFromCameraLayout = dialog.findViewById(R.id.layoutAddFromCamera);
//        LinearLayout addRecipeLayout = dialog.findViewById(R.id.layoutAddRecipe);
        LinearLayout llBreakfast = dialog.findViewById(R.id.llBreakfast);
        LinearLayout llLunch = dialog.findViewById(R.id.llLunch);
        LinearLayout llDinner = dialog.findViewById(R.id.llDinner);
        LinearLayout llSnacks = dialog.findViewById(R.id.llSnacks);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

//        addFromCameraLayout.setOnClickListener(v -> {
//            dialog.dismiss();
//            mealClickListener.onAddFromCameraSelected();
//        });
//
//        addRecipeLayout.setOnClickListener(v -> {
//            dialog.dismiss();
//            mealClickListener.onAddRecipeSelected();
//        });

        llBreakfast.setOnClickListener(v -> {
            dialog.dismiss();
            mealClickListener.onMealSelected(Meal.BREAKFAST);
        });

        llLunch.setOnClickListener(v -> {
            dialog.dismiss();
            mealClickListener.onMealSelected(Meal.LUNCH);
        });

        llDinner.setOnClickListener(v -> {
            dialog.dismiss();
            mealClickListener.onMealSelected(Meal.DINNER);
        });

        llSnacks.setOnClickListener(v -> {
            dialog.dismiss();
            mealClickListener.onMealSelected(Meal.SNACKS);
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * A listener interface to handle meal option selection in the bottom sheet dialog.
     * This will be used in the activity/fragment to respond to the meal selection.
     */
    public interface MealClickListener {
        void onMealSelected(String meal);
//        void onAddFromCameraSelected();
//        void onAddRecipeSelected();

    }

    /**
     * Shows a bottom sheet dialog to choose between Camera or Gallery.
     *
     * @param context  Context for dialog creation.
     * @param listener Listener for handling camera/gallery selection.
     */
    public static void showImageSourceDialog(Context context, MediaClickListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_add_from_camera);

        ImageView ivCamera = dialog.findViewById(R.id.ivCamera);
        ImageView ivGallery = dialog.findViewById(R.id.ivGallery);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        ivCamera.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) listener.onCameraSelected();
        });

        ivGallery.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) listener.onGallerySelected();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    }

    /**
     * A listener interface to handle image source selection (Camera or Gallery).
     */
    public interface MediaClickListener {
        void onCameraSelected();
        void onGallerySelected();
    }
}
