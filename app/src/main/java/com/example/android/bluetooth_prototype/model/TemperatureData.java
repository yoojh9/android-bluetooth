package com.example.android.bluetooth_prototype.model;


public class TemperatureData {
    private String date;
    private double currentTemp;
    private double refTemp;
    private String mode;


    public void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setRefTemp(double refTemp) {
        this.refTemp = refTemp;
    }

    public double getCurrentTemp() {
        return currentTemp;
    }

    public double getRefTemp() {
        return refTemp;
    }

    public String getDate() {
        return date;
    }

    public String getMode() {
        return mode;
    }
}

