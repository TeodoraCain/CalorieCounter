package com.example.caloriecounter.models.dao;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("unused")
public class WeightLog implements Parcelable {
    private String date;
    private double weight;

    private String frontPictureUri,
            sidePictureUri,
            backPictureUri;

    public WeightLog() {
    }

    public WeightLog(double weight) {
        Date date = Calendar.getInstance().getTime();
        this.date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
        this.weight = weight;
        this.frontPictureUri = "";
        this.backPictureUri = "";
        this.sidePictureUri = "";
    }

    public String getFrontPictureUri() {
        return frontPictureUri;
    }

    public void setFrontPictureUri(String frontPictureUri) {
        this.frontPictureUri = frontPictureUri;
    }

    public String getSidePictureUri() {
        return sidePictureUri;
    }

    public void setSidePictureUri(String sidePictureUri) {
        this.sidePictureUri = sidePictureUri;
    }

    public String getBackPictureUri() {
        return backPictureUri;
    }

    public void setBackPictureUri(String backPictureUri) {
        this.backPictureUri = backPictureUri;
    }

    public WeightLog(String date, double weight, String frontPictureUri, String sidePictureUri, String backPictureUri) {
        this.date = date;
        this.weight = weight;
        this.frontPictureUri = frontPictureUri;
        this.sidePictureUri = sidePictureUri;
        this.backPictureUri = backPictureUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    protected WeightLog(Parcel in){
        this.date = in.readString();
        this.weight = in.readDouble();
        this.frontPictureUri = in.readString();
        this.sidePictureUri = in.readString();
        this.backPictureUri = in.readString();
    }

    public static final Creator<WeightLog> CREATOR = new Creator<WeightLog>() {
        @Override
        public WeightLog createFromParcel(Parcel in) {
            return new WeightLog(in);
        }

        @Override
        public WeightLog[] newArray(int size) {
            return new WeightLog[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeDouble(weight);
        dest.writeString(frontPictureUri);
        dest.writeString(sidePictureUri);
        dest.writeString(backPictureUri);
    }
}
