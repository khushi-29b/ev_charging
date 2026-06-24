package com.ev.model;

public class ChargingSession {
     private int    sessionId;
    private int    connectorId;
    private int    vehicleId;
    private String startTime;
    private String endTime;
    private double totalKwh;
    private String connectorType;
    private String vehicleMake;
 
    public int    getSessionId()     { return sessionId; }
    public int    getConnectorId()   { return connectorId; }
    public int    getVehicleId()     { return vehicleId; }
    public String getStartTime()     { return startTime; }
    public String getEndTime()       { return endTime; }
    public double getTotalKwh()      { return totalKwh; }
    public String getConnectorType() { return connectorType; }
    public String getVehicleMake()   { return vehicleMake; }
 
    public void setSessionId(int v)      { sessionId = v; }
    public void setConnectorId(int v)    { connectorId = v; }
    public void setVehicleId(int v)      { vehicleId = v; }
    public void setStartTime(String v)   { startTime = v; }
    public void setEndTime(String v)     { endTime = v; }
    public void setTotalKwh(double v)    { totalKwh = v; }
    public void setConnectorType(String v){ connectorType = v; }
    public void setVehicleMake(String v) { vehicleMake = v; }
}
