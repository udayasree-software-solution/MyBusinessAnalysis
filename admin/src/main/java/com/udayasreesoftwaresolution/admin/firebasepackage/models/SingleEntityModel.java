package com.udayasreesoftwaresolution.admin.firebasepackage.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SingleEntityModel implements Parcelable {
    private String inputData;

    public SingleEntityModel() {
    }

    public SingleEntityModel(String inputData) {
        this.inputData = inputData;
    }

    protected SingleEntityModel(Parcel in) {
        inputData = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inputData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SingleEntityModel> CREATOR = new Creator<SingleEntityModel>() {
        @Override
        public SingleEntityModel createFromParcel(Parcel in) {
            return new SingleEntityModel(in);
        }

        @Override
        public SingleEntityModel[] newArray(int size) {
            return new SingleEntityModel[size];
        }
    };

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }
}
