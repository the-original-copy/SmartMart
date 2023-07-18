package com.smartherd.smartmart.models

import android.os.Parcel
import android.os.Parcelable

data class Order(
    var id: String = "",
    var productID: String = "",
    var customerID: String = "",
    var farmerID: String = "",
    var farmerName: String = "",
    var quantity: Int = 0,
    var totalPrice: Int = 0,
    val complete: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(productID)
        parcel.writeString(customerID)
        parcel.writeString(farmerID)
        parcel.writeString(farmerName)
        parcel.writeInt(quantity)
        parcel.writeInt(totalPrice)
        parcel.writeString(complete)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}
