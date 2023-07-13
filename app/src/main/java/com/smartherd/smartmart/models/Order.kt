package com.smartherd.smartmart.models

import android.os.Parcel
import android.os.Parcelable

data class Order(
    var id: String = "",
    var productID: String = "",
    var customerID: String = "",
    var farmerID: String = "",
    var quantity: Int = 0,
    var totalPrice: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(productID)
        parcel.writeString(customerID)
        parcel.writeString(farmerID)
        parcel.writeInt(quantity)
        parcel.writeInt(totalPrice)
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
