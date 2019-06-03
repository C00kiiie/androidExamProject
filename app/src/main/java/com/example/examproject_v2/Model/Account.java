package com.example.examproject_v2.Model;

public class Account {

    private String acountType;
    private double balance;
    private boolean status;

    public Account (){

    }

    public String getAcountType() {
        return acountType;
    }

    public void setAcountType(String acountType) {
        this.acountType = acountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Account(String acountType, double balance, boolean status) {
        this.acountType = acountType;
        this.balance = balance;
        this.status = status;
    }
}
