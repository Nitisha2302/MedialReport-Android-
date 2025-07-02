package com.medicalreport.modal.response

import android.net.Uri
import android.os.Parcel


//data class ImageData(val image: Uri)
import android.os.Parcelable

data class ImageData(val image: Uri) : Parcelable {
    constructor(parcel: Parcel) : this(Uri.parse(parcel.readString()!!))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(image.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageData> {
        override fun createFromParcel(parcel: Parcel): ImageData {
            return ImageData(parcel)
        }

        override fun newArray(size: Int): Array<ImageData?> {
            return arrayOfNulls(size)
        }
    }
}