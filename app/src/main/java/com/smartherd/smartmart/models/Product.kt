package com.smartherd.smartmart.models

import android.os.Parcel
import android.os.Parcelable

data class Product(
    var id: String = "",
    val productName: String = "",
    val productDescription: String = "",
    val productCategory: String = "",
    val productPrice: Int = 0,
    val productImage: String = "",
    val farmerID: String = "",
    val farmerName: String = "",
    val location: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val average_location: String = "",
    var productNumberOfOrders: Int = 0,
    val uniqueConfirmationID: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(productName)
        parcel.writeString(productDescription)
        parcel.writeString(productCategory)
        parcel.writeInt(productPrice)
        parcel.writeString(productImage)
        parcel.writeString(farmerID)
        parcel.writeString(farmerName)
        parcel.writeString(location)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeString(average_location)
        parcel.writeInt(productNumberOfOrders)
        parcel.writeInt(uniqueConfirmationID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
