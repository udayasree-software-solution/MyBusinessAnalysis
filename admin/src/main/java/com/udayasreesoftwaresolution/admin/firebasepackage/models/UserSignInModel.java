package com.udayasreesoftwaresolution.admin.firebasepackage.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserSignInModel implements Parcelable {
    private String userId, userName, userMobile, userOutlet, verificationCode, loginUserType,deviceLoginCode;

    public UserSignInModel() {
    }

    public UserSignInModel(String userId, String userName, String userMobile, String userOutlet,
                           String verificationCode, String loginUserType, String deviceLoginCode) {
        this.userId = userId;
        this.userName = userName;
        this.userMobile = userMobile;
        this.userOutlet = userOutlet;
        this.verificationCode = verificationCode;
        this.loginUserType = loginUserType;
        this.deviceLoginCode = deviceLoginCode;
    }

    protected UserSignInModel(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userMobile = in.readString();
        userOutlet = in.readString();
        verificationCode = in.readString();
        loginUserType = in.readString();
        deviceLoginCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userMobile);
        dest.writeString(userOutlet);
        dest.writeString(verificationCode);
        dest.writeString(loginUserType);
        dest.writeString(deviceLoginCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserSignInModel> CREATOR = new Creator<UserSignInModel>() {
        @Override
        public UserSignInModel createFromParcel(Parcel in) {
            return new UserSignInModel(in);
        }

        @Override
        public UserSignInModel[] newArray(int size) {
            return new UserSignInModel[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserOutlet() {
        return userOutlet;
    }

    public void setUserOutlet(String userOutlet) {
        this.userOutlet = userOutlet;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getLoginUserType() {
        return loginUserType;
    }

    public void setLoginUserType(String loginUserType) {
        this.loginUserType = loginUserType;
    }

    public String getDeviceLoginCode() {
        return deviceLoginCode;
    }

    public void setDeviceLoginCode(String deviceLoginCode) {
        this.deviceLoginCode = deviceLoginCode;
    }
}