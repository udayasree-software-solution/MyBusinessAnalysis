package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PaymentModel implements Parcelable {
    private String uniqueKey, clientName, payAmount, chequeNumber;
    private long dateInMillis;
    private boolean payStatus;
    private int preDays;

    public PaymentModel() {
    }

    public PaymentModel(String uniqueKey, String clientName, String payAmount,
                        String chequeNumber, long dateInMillis, boolean payStatus, int preDays) {
        this.uniqueKey = uniqueKey;
        this.clientName = clientName;
        this.payAmount = payAmount;
        this.chequeNumber = chequeNumber;
        this.dateInMillis = dateInMillis;
        this.payStatus = payStatus;
        this.preDays = preDays;
    }

    protected PaymentModel(Parcel in) {
        uniqueKey = in.readString();
        clientName = in.readString();
        payAmount = in.readString();
        chequeNumber = in.readString();
        dateInMillis = in.readLong();
        payStatus = in.readByte() != 0;
        preDays = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uniqueKey);
        dest.writeString(clientName);
        dest.writeString(payAmount);
        dest.writeString(chequeNumber);
        dest.writeLong(dateInMillis);
        dest.writeByte((byte) (payStatus ? 1 : 0));
        dest.writeInt(preDays);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentModel> CREATOR = new Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(Parcel in) {
            return new PaymentModel(in);
        }

        @Override
        public PaymentModel[] newArray(int size) {
            return new PaymentModel[size];
        }
    };

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public boolean getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(boolean payStatus) {
        this.payStatus = payStatus;
    }

    public int getPreDays() {
        return preDays;
    }

    public void setPreDays(int preDays) {
        this.preDays = preDays;
    }
}
