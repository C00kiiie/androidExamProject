package com.example.examproject_v2.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class PaymentParcelable implements Parcelable {

    private String dateText;
    private String billName;
    private String accountSpinner;
    private int kontoNummer;

    public PaymentParcelable() {
    }

    private int amount;

    public PaymentParcelable(String accountSpinner, int kontoNummer, int amount) {
        this.accountSpinner = accountSpinner;
        this.kontoNummer = kontoNummer;
        this.amount = amount;
    }

    public PaymentParcelable(String dateText, String billName, String accountSpinner, int kontoNummer, int amount) {
        this.dateText = dateText;
        this.billName = billName;
        this.accountSpinner = accountSpinner;
        this.kontoNummer = kontoNummer;
        this.amount = amount;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getAccountSpinner() {
        return accountSpinner;
    }

    public void setAccountSpinner(String accountSpinner) {
        this.accountSpinner = accountSpinner;
    }

    public int getKontoNummer() {
        return kontoNummer;
    }

    public void setKontoNummer(int kontoNummer) {
        this.kontoNummer = kontoNummer;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    protected PaymentParcelable(Parcel in) {
        dateText = in.readString();
        billName = in.readString();
        accountSpinner = in.readString();
        kontoNummer = in.readInt();
        amount = in.readInt();
    }

    public static final Creator<PaymentParcelable> CREATOR = new Creator<PaymentParcelable>() {
        @Override
        public PaymentParcelable createFromParcel(Parcel in) {
            return new PaymentParcelable(in);
        }

        @Override
        public PaymentParcelable[] newArray(int size) {
            return new PaymentParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateText);
        dest.writeString(billName);
        dest.writeString(accountSpinner);
        dest.writeInt(kontoNummer);
        dest.writeInt(amount);
    }
}
