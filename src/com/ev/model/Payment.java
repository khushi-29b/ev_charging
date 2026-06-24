package com.ev.model;

public class Payment {
    private int    paymentId;
    private int    sessionId;
    private String paymentMethod;
    private double amount;

    public int    getPaymentId()     { return paymentId; }
    public int    getSessionId()     { return sessionId; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getAmount()        { return amount; }

    public void setPaymentId(int v)       { paymentId = v; }
    public void setSessionId(int v)       { sessionId = v; }
    public void setPaymentMethod(String v){ paymentMethod = v; }
    public void setAmount(double v)       { amount = v; }
}