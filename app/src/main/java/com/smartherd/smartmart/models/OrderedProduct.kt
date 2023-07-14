package com.smartherd.smartmart.models

import android.os.Parcel
import android.os.Parcelable

class OrderedProduct (
    var productName: String = "",
    var productImage: String = "",
    var orderID: String = "",
    var totalPrice: Int = 0,
    var quantity: Int = 0) {

    @JvmName("setProductName1")
    fun setProductName(productName: String){
        this.productName = productName
    }
}