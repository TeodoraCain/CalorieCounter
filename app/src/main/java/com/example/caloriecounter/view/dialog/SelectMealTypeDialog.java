package com.example.caloriecounter.view.dialog;

import static com.example.caloriecounter.R.id;
import static com.example.caloriecounter.R.layout;
import static com.example.caloriecounter.R.string;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.caloriecounter.AddFoodActivity;
import com.example.caloriecounter.models.dataModel.IntentKeys;

import java.util.Objects;

public class SelectMealTypeDialog extends AppCompatDialogFragment {

//    DialogListener listener;

    public SelectMealTypeDialog() {
    }

    @SuppressLint("NonConstantResourceId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        Context context = getContext();
        View view = inflater.inflate(layout.cutom_dialog_select_meal_type, new FrameLayout(context), false);

        builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {
        });

        RadioGroup rgMealType = view.findViewById(id.rgMealType);
        rgMealType.setOnCheckedChangeListener((group, checkedId) -> {
            String mealType = getResources().getString(string.breakfast);

            // Check which radio button is selected
            switch (checkedId) {
                case id.rbBreakfast:
                    mealType = getResources().getString(string.breakfast);
                    break;
                case id.rbLunch:
                    mealType = getResources().getString(string.lunch);
                    break;
                case id.rbDinner:
                    mealType = getResources().getString(string.dinner);
                    break;
                case id.rbSnacks:
                    mealType = getResources().getString(string.snacks);
                    break;
            }
            Intent intent = new Intent(context, AddFoodActivity.class);
            intent.putExtra(IntentKeys.MEAL, mealType);
            startActivity(intent);
        });

        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//
//        try {
//            listener = (DialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString());
//        }
    }

//    public interface DialogListener {
//        void applyText(String text, TextView textView);
//    }
}
