package com.example.caloriecounter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeightLogSliderItemAdapter extends RecyclerView.Adapter<WeightLogSliderItemAdapter.WeightLogSliderViewHolder> {

    private final List<WeightLogSliderItem> sliderItems;
    private final ProgressBar progressBar;

    public WeightLogSliderItemAdapter(List<WeightLogSliderItem> sliderItems, ProgressBar progressBar) {
        this.sliderItems = sliderItems;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public WeightLogSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeightLogSliderViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_slider_item_container, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WeightLogSliderViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class WeightLogSliderViewHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView imageView;

        WeightLogSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlider);
        }

        void setImage(com.example.caloriecounter.adapters.WeightLogSliderItem sliderItem) {
            String imageUrl = sliderItem.getImage();
            if (!imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }
    }
}
