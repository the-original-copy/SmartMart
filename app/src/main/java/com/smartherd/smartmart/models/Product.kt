package com.smartherd.smartmart.models

import android.os.Parcel
import android.os.Parcelable

data class Product(
    var id: String = "",
    val productName: String = "",
    val productDescription: String = "",
    val productCategory: String = "",
    val productPrice: String = "",
    val productImage: String = "",
    val farmerID: String = "",
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
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(productName)
        parcel.writeString(productDescription)
        parcel.writeString(productCategory)
        parcel.writeString(productPrice)
        parcel.writeString(productImage)
        parcel.writeString(farmerID)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeString(postal)
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
