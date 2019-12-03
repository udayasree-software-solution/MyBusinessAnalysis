package com.udayasreesoftwaresolution.mybusinessanalysis.ui.model

import android.os.Parcel
import android.os.Parcelable

class AmountModel(val title : String, val total : Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(total)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AmountModel> {
        override fun createFromParcel(parcel: Parcel): AmountModel {
            return AmountModel(parcel)
        }

        override fun newArray(size: Int): Array<AmountModel?> {
            return arrayOfNulls(size)
        }
    }

}