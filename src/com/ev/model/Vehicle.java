package com.ev.model;

public class Vehicle {
    private int    vehicleId;
    private int    userId;
    private String make;
    private double batteryCapacity;

    public int    getVehicleId()       { return vehicleId; }
    public int    getUserId()          { return userId; }
    public String getMake()            { return make; }
    public double getBatteryCapacity() { return batteryCapacity; }

    public void setVehicleId(int v)         { vehicleId = v; }
    public void setUserId(int v)            { userId = v; }
    public void setMake(String v)           { make = v; }
    public void setBatteryCapacity(double v){ batteryCapacity = v; }
}