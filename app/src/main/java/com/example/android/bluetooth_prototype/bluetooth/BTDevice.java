package com.example.android.bluetooth_prototype.bluetooth;

public class BTDevice {
    private String name;
    private String address;

    public BTDevice(String name, String address){
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BTDevice{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}