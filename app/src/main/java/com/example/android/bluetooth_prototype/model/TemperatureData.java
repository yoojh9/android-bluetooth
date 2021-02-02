package com.example.android.bluetooth_prototype.model;


public class TemperatureData {
    private String dateTime;
    private double currentTemp;
    private double refTemp;
    private String mode;


    public void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public String getDateTime() {
        return dateTime;
    }

    public String getMode() {
        return mode;
    }
}

