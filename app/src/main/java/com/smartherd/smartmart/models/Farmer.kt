package com.smartherd.smartmart.models

import android.os.Parcel
import android.os.Parcelable

data class Farmer(
val id: String = "",
val name : String = "",
val email : String = "",
val mobile : String = "",
val location: String = "",
val longitude: Double = 0.0,
val latitude: Double = 0.0,
val postal: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(mobile)
        parcel.writeString(location)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeString(postal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Farmer> {
        override fun createFromParcel(parcel: Parcel): Farmer {
            return Farmer(parcel)
        }

        override fun newArray(size: Int): Array<Farmer?> {
            return arrayOfNulls(size)
        }
    }
}
