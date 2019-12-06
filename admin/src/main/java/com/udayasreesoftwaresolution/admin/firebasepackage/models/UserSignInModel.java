package com.udayasreesoftwaresolution.admin.firebasepackage.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserSignInModel implements Parcelable {
    private String userId, userName, userMobile, registerDate, userOutlet, verificationCode, deviceLoginCode;
    private boolean isCodeVerified, isAdmin;

    public UserSignInModel() {
    }

    public UserSignInModel(String userId, String userName, String userMobile, String userOutlet, String verificationCode, boolean isCodeVerified, boolean isAdmin, String deviceLoginCode) {
        this.userId = userId;
        this.userName = userName;
        this.userMobile = userMobile;
        this.userOutlet = userOutlet;
        this.verificationCode = verificationCode;
        this.isCodeVerified = isCodeVerified;
        this.isAdmin = isAdmin;
        this.deviceLoginCode = deviceLoginCode;
    }

    protected UserSignInModel(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userMobile = in.readString();
        registerDate = in.readString();
        userOutlet = in.readString();
        verificationCode = in.readString();
        isCodeVerified = in.readByte() != 0;
        isAdmin = in.readByte() != 0;
        deviceLoginCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userMobile);
        dest.writeString(registerDate);
        dest.writeString(userOutlet);
        dest.writeString(verificationCode);
        dest.writeByte((byte) (isCodeVerified ? 1 : 0));
        dest.writeByte((byte) (isAdmin ? 1 : 0));
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

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
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

    public boolean getCodeVerified() {
        return isCodeVerified;
    }

    public void setCodeVerified(boolean codeVerified) {
        isCodeVerified = codeVerified;
    }

    public boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getDeviceLoginCode() {
        return deviceLoginCode;
    }

    public void setDeviceLoginCode(String deviceLoginCode) {
        this.deviceLoginCode = deviceLoginCode;
    }
}