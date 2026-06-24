package com.ev.model;

public class User {
    private int    userId;
    private String kycStatus;

    public int    getUserId()    { return userId; }
    public String getKycStatus() { return kycStatus; }

    public void setUserId(int v)      { userId = v; }
    public void setKycStatus(String v){ kycStatus = v; }
}