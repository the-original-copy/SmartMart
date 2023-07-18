package com.smartherd.smartmart.models

import android.os.Parcel
import android.os.Parcelable

class OrderedProduct (
    var productName: String = "",
    var productImage: String = "",
    var orderID: String = "",
    var totalPrice: Int = 0,
    var quantity: Int = 0,
    var customerID: String = "",
    var farmerID: String = "",
    var farmerName: String = "",
    var uniqueProductID: Int = 0
    )