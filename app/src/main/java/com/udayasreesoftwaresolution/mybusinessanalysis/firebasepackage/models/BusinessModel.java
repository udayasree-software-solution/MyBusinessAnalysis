package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessModel implements Parcelable {
    private int order, amount;
    private String businessName, selectedDate;
    private long timeInMillis;

    public BusinessModel(int order, String businessName, int amount, String selectedDate, long timeInMillis) {
        this.order = order;
        this.businessName = businessName;
        this.amount = amount;
        this.selectedDate = selectedDate;
        this.timeInMillis = timeInMillis;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    protected BusinessModel(Parcel in) {
        order = in.readInt();
        businessName = in.readString();
        amount = in.readInt();
        selectedDate = in.readString();
        timeInMillis = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(order);
        dest.writeString(businessName);
        dest.writeInt(amount);
        dest.writeString(selectedDate);
        dest.writeLong(timeInMillis);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusinessModel> CREATOR = new Creator<BusinessModel>() {
        @Override
        public BusinessModel createFromParcel(Parcel in) {
            return new BusinessModel(in);
        }

        @Override
        public BusinessModel[] newArray(int size) {
            return new BusinessModel[size];
        }
    };
}
