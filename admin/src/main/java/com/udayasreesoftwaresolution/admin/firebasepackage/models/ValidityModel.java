package com.udayasreesoftwaresolution.admin.firebasepackage.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ValidityModel implements Parcelable {

    private long premiumDate, validityDate;
    private int premiumAmount, planInDays;

    public ValidityModel() {
    }

    public ValidityModel(long premiumDate, long validityDate, int premiumAmount, int planInDays) {
        this.premiumDate = premiumDate;
        this.validityDate = validityDate;
        this.premiumAmount = premiumAmount;
        this.planInDays = planInDays;
    }

    protected ValidityModel(Parcel in) {
        premiumDate = in.readLong();
        validityDate = in.readLong();
        premiumAmount = in.readInt();
        planInDays = in.readInt();
    }

    public static final Creator<ValidityModel> CREATOR = new Creator<ValidityModel>() {
        @Override
        public ValidityModel createFromParcel(Parcel in) {
            return new ValidityModel(in);
        }

        @Override
        public ValidityModel[] newArray(int size) {
            return new ValidityModel[size];
        }
    };

    public long getPremiumDate() {
        return premiumDate;
    }

    public void setPremiumDate(long premiumDate) {
        this.premiumDate = premiumDate;
    }

    public long getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(long validityDate) {
        this.validityDate = validityDate;
    }

    public int getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(int premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public int getPlanInDays() {
        return planInDays;
    }

    public void setPlanInDays(int planInDays) {
        this.planInDays = planInDays;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(premiumDate);
        dest.writeLong(validityDate);
        dest.writeInt(premiumAmount);
        dest.writeInt(planInDays);
    }
}
