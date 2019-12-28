package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessModel implements Parcelable {
    private int ascOrder, amount;
    private String businessName, businessCategory, selectedDate;
    private long timeInMillis;

    public BusinessModel() {
    }

    public BusinessModel(int ascOrder, int amount, String businessName, String businessCategory, String selectedDate, long timeInMillis) {
        this.ascOrder = ascOrder;
        this.amount = amount;
        this.businessName = businessName;
        this.businessCategory = businessCategory;
        this.selectedDate = selectedDate;
        this.timeInMillis = timeInMillis;
    }

    protected BusinessModel(Parcel in) {
        ascOrder = in.readInt();
        amount = in.readInt();
        businessName = in.readString();
        businessCategory = in.readString();
        selectedDate = in.readString();
        timeInMillis = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ascOrder);
        dest.writeInt(amount);
        dest.writeString(businessName);
        dest.writeString(businessCategory);
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

    public int getAscOrder() {
        return ascOrder;
    }

    public void setAscOrder(int ascOrder) {
        this.ascOrder = ascOrder;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
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
}
