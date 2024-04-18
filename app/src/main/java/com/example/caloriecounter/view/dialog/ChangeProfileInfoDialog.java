package com.example.caloriecounter.view.dialog;

import static com.example.caloriecounter.R.array;
import static com.example.caloriecounter.R.id;
import static com.example.caloriecounter.R.layout;
import static com.example.caloriecounter.R.string;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class ChangeProfileInfoDialog extends AppCompatDialogFragment {

    private final TextView TEXT_VIEW;
    DialogListener listener;
    private EditText etName;
    private EditText etDOB;
    private String newGender;
    private Spinner spCountry;
    private String newCountry;

    public ChangeProfileInfoDialog(TextView TEXT_VIEW) {
        this.TEXT_VIEW = TEXT_VIEW;
    }

    @SuppressLint("NonConstantResourceId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        Context context = getContext();
        View view;
        switch (TEXT_VIEW.getId()) {
            case id.tvName:
                view = inflater.inflate(layout.cutom_dialog_change_name, new FrameLayout(context), false);

                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {
                }).setPositiveButton("Ok", (dialog, which) -> {
                    String name = String.valueOf(etName.getText());
                    listener.applyText(name, TEXT_VIEW);
                });

                etName = view.findViewById(id.etChangeName);
                break;
            case id.tvDOB:
                view = inflater.inflate(layout.cutom_dialog_change_dob, new FrameLayout(context), false);

                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Ok", (dialog, which) -> {
                    String dob = String.valueOf(etDOB.getText());
                    listener.applyText(dob, TEXT_VIEW);
                });

                etDOB = view.findViewById(id.etChangeDOB);
                etDOB.setOnClickListener(v -> showDatePicker());
                break;
            case id.tvGender:
                view = inflater.inflate(layout.cutom_dialog_change_gender, new FrameLayout(context), false);

                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Ok", (dialog, which) -> {
                    if (!Objects.equals(newGender, null)) {
                        listener.applyText(newGender, TEXT_VIEW);
                    }
                });

                RadioGroup rgGender = view.findViewById(id.rgChangeGender);
                rgGender.setOnCheckedChangeListener((group, checkedId) -> {
                    // Check which radio button is selected
                    if (checkedId == id.rbChangeFemale) {
                        // Female radio button is selected
                        newGender = getResources().getString(string.female_text);

                    } else if (checkedId == id.rbChangeMale) {
                        // Male radio button is selected
                        newGender = getResources().getString(string.male_text);
                    }
                });
                break;
            case id.tvCountry:
                view = inflater.inflate(layout.cutom_dialog_change_country, new FrameLayout(context), false);

                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Ok", (dialog, which) -> {
                    if (!Objects.equals(newCountry, null)) {
                        listener.applyText(newCountry, TEXT_VIEW);
                    } else {
                        listener.applyText(spCountry.getItemAtPosition(227).toString(), TEXT_VIEW);
                    }
                });


                spCountry = view.findViewById(id.spChangeCountry);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), array.countries_array, layout.custom_spinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spCountry.setAdapter(adapter);
                spCountry.setSelection(227, true);

                spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        newCountry = spCountry.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
        }

        return builder.create();
    }


    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), (view, year1, month1, dayOfMonth1) -> {
            String selectedDate = dayOfMonth1 + "/" + (month1 + 1) + "/" + year1;
            etDOB.setText(selectedDate);
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    public interface DialogListener {
        void applyText(String text, TextView textView);
    }
}
