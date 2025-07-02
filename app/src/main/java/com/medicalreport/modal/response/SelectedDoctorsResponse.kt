package com.medicalreport.modal.response

import android.os.Parcel
import android.os.Parcelable

data class SelectedDoctorsResponse(
    val id: Int,
    val name: String,
    val specialty: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(specialty)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectedDoctorsResponse> {
        override fun createFromParcel(parcel: Parcel): SelectedDoctorsResponse {
            return SelectedDoctorsResponse(parcel)
        }

        override fun newArray(size: Int): Array<SelectedDoctorsResponse?> {
            return arrayOfNulls(size)
        }
    }
}
