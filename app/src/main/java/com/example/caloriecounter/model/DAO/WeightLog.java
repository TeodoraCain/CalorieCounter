package com.example.caloriecounter.model.DAO;

public class WeightLog {
    private String date;
    private double weight;

    private String frontPictureUri,
            sidePictureUri,
            backPictureUri;

    public WeightLog() {
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
}
