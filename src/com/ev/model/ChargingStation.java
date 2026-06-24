package com.ev.model;

public class ChargingStation {
     private int    stationId;
    private String operatorName;
    private double latitude;
    private double longitude;
    private int    discomId;
    private int    transformerId;
    private String discomName;
 
    public int    getStationId()     { return stationId; }
    public String getOperatorName()  { return operatorName; }
    public double getLatitude()      { return latitude; }
    public double getLongitude()     { return longitude; }
    public int    getDiscomId()      { return discomId; }
    public int    getTransformerId() { return transformerId; }
    public String getDiscomName()    { return discomName; }
 
    public void setStationId(int v)      { stationId = v; }
    public void setOperatorName(String v){ operatorName = v; }
    public void setLatitude(double v)    { latitude = v; }
    public void setLongitude(double v)   { longitude = v; }
    public void setDiscomId(int v)       { discomId = v; }
    public void setTransformerId(int v)  { transformerId = v; }
    public void setDiscomName(String v)  { discomName = v; }
}
