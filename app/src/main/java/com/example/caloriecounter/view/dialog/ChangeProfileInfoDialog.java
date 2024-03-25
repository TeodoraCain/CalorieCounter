package com.example.caloriecounter.view.dialog;

import static com.example.caloriecounter.R.array;
import static com.example.caloriecounter.R.id;
import static com.example.caloriecounter.R.layout;
import static com.example.caloriecounter.R.string;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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
    private EditText etName;
    private EditText etDOB;
    private final TextView textView;
    private RadioGroup rgGender;
    private String newGender;
    private Spinner spCountry;
    private String newCountry;
    DialogListener listener;

    public ChangeProfileInfoDialog(TextView textView) {
        this.textView = textView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;
        switch (textView.getId()) {
            case id.tvName:
                view = inflater.inflate(layout.change_name_dialog, null);

                builder.setView(view)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = String.valueOf(etName.getText());
                                listener.applyText(name, textView);
                            }
                        });

                etName = view.findViewById(id.etChangeName);
                break;
            case id.tvDOB:
                view = inflater.inflate(layout.change_dob_dialog, null);

                builder.setView(view)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String dob = String.valueOf(etDOB.getText());
                                listener.applyText(dob, textView);
                            }
                        });

                etDOB = view.findViewById(id.etChangeDOB);
                etDOB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker();
                    }
                });
                break;
            case id.tvGender:
                view = inflater.inflate(layout.change_gender_dialog, null);

                builder.setView(view)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!Objects.equals(newGender, null)) {
                                    listener.applyText(newGender, textView);
                                }
                            }
                        });


                rgGender = view.findViewById(id.rgChangeGender);
                rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // Check which radio button is selected
                        if (checkedId == id.rbChangeFemale) {
                            // Female radio button is selected
                            newGender = getResources().getString(string.female_text);

                        } else if (checkedId == id.rbChangeMale) {
                            // Male radio button is selected
                            newGender = getResources().getString(string.male_text);
                        }
                    }
                });
                break;
            case id.tvCountry:
                view = inflater.inflate(layout.change_country_dialog, null);

                builder.setView(view)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!Objects.equals(newCountry, null)) {
                                    listener.applyText(newCountry, textView);
                                }else {
                                    listener.applyText(spCountry.getItemAtPosition(227).toString(), textView);
                                }
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        etDOB.setText(selectedDate);
                    }
                },
                year, month, dayOfMonth);

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
