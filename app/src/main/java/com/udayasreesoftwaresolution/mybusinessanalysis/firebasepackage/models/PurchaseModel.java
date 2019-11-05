package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PurchaseModel implements Parcelable {
    private long timeInMillis;
    private String dateOfPurchase, clientName, billNo, billAmount;

    public PurchaseModel() {
    }

    public PurchaseModel(long timeInMillis, String dateOfPurchase, String clientName, String billNo, String billAmount) {
        this.timeInMillis = timeInMillis;
        this.dateOfPurchase = dateOfPurchase;
        this.clientName = clientName;
        this.billNo = billNo;
        this.billAmount = billAmount;
    }

    protected PurchaseModel(Parcel in) {
        timeInMillis = in.readLong();
        dateOfPurchase = in.readString();
        clientName = in.readString();
        billNo = in.readString();
        billAmount = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timeInMillis);
        dest.writeString(dateOfPurchase);
        dest.writeString(clientName);
        dest.writeString(billNo);
        dest.writeString(billAmount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PurchaseModel> CREATOR = new Creator<PurchaseModel>() {
        @Override
        public PurchaseModel createFromParcel(Parcel in) {
            return new PurchaseModel(in);
        }

        @Override
        public PurchaseModel[] newArray(int size) {
            return new PurchaseModel[size];
        }
    };

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }
}
