package com.smartherd.smartmart.models

import android.os.Parcel
import android.os.Parcelable

data class Customer(
    val id: String = "",
    val name : String = "",
    val email : String = "",
    val mobile : String = "",
    val location: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val average_location: String = "",
    val local_area_name: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
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
        parcel.writeString(average_location)
        parcel.writeString(local_area_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Customer> {
        override fun createFromParcel(parcel: Parcel): Customer {
            return Customer(parcel)
        }

        override fun newArray(size: Int): Array<Customer?> {
            return arrayOfNulls(size)
        }
    }
}
