package com.ev.model;

public class MaintenanceTicket {
    private int    ticketId;
    private int    stationId;
    private String issueDesc;
    private String openedTime;
    private String closedTime;
    private String operatorName;

    public int    getTicketId()     { return ticketId; }
    public int    getStationId()    { return stationId; }
    public String getIssueDesc()    { return issueDesc; }
    public String getOpenedTime()   { return openedTime; }
    public String getClosedTime()   { return closedTime; }
    public String getOperatorName() { return operatorName; }

    public void setTicketId(int v)      { ticketId = v; }
    public void setStationId(int v)     { stationId = v; }
    public void setIssueDesc(String v)  { issueDesc = v; }
    public void setOpenedTime(String v) { openedTime = v; }
    public void setClosedTime(String v) { closedTime = v; }
    public void setOperatorName(String v){ operatorName = v; }
}