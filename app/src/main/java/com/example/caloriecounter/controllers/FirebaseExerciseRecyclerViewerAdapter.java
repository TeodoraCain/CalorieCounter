package com.example.caloriecounter.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.model.DAO.Exercise;
import com.example.caloriecounter.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class FirebaseExerciseRecyclerViewerAdapter extends FirebaseRecyclerAdapter<Exercise, FirebaseExerciseRecyclerViewerAdapter.RecyclerViewHolder> {

    private RecyclerViewInterface recyclerViewInterface;

    public FirebaseExerciseRecyclerViewerAdapter(@NonNull FirebaseRecyclerOptions<Exercise> options, RecyclerViewInterface recyclerViewInterface) {
        super(options);
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public FirebaseExerciseRecyclerViewerAdapter(@NonNull FirebaseRecyclerOptions<Exercise> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position, @NonNull Exercise model) {
        holder.exercise.setText(model.getName());
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        return new RecyclerViewHolder(view, recyclerViewInterface);
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView exercise;

        public RecyclerViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            exercise = itemView.findViewById(R.id.tvExercise);

            itemView.setOnClickListener(v -> {
                if(recyclerViewInterface !=null){
                    int position = getAbsoluteAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position);
                        Log.d("interface click", String.valueOf(position));
                    }
                }
            });
        }
    }


}
