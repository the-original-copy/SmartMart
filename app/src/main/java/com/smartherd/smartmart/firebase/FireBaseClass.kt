package com.smartherd.smartmart.firebase

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.smartherd.smartmart.activities.*
import com.smartherd.smartmart.adapter.OrdersListAdapter
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.models.*
import com.smartherd.smartmart.utils.Constants

open class FireBaseClass : BaseActivity() {
    private val mFireStore = Firebase.firestore
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        basebinding = DialogProgressBinding.inflate(layoutInflater)
        basebinding.root.let {
            mProgressDialog.setContentView(it)
        }
        basebinding.tvProgressText.text = text
        mProgressDialog.show()
    }
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
    fun registerUser(activity : Activity, userInfo : User) {
        Log.e("Firestore", "has been accessed")
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("FireStore", "has been accessed and is successful ")
                when(activity) {
                    is UserSignUp -> {
                        activity.userRegisteredSuccess()
                    }
                    is FarmerSignUp -> {
                        activity.userRegisteredSuccess()
                    }
                }

            }
            .addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
    }

    fun createFarmer(activity : FarmerSignUp, farmer : Farmer) {
        mFireStore.collection(Constants.FARMERS)
            .document(farmer.id)
            .set(farmer, SetOptions.merge())
            .addOnSuccessListener {
                activity.farmerCreated()
            }
            .addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
    }

    fun createCustomer(activity: UserSignUp, customer : Customer) {
        mFireStore.collection(Constants.CUSTOMERS)
            .document(customer.id)
            .set(customer, SetOptions.merge())
            .addOnSuccessListener {
                activity.customerCreated()
            }
            .addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
    }

    fun createOrder(activity: OrderConfirmation,order: Order) {
        mFireStore.collection(Constants.ORDER)
            .add(order)
            .addOnSuccessListener { document ->
                if(order.id == ""){
                    order.id = document.id
                    activity.setOrderID(order.id) }
                activity.orderCreatedSuccessfully() }
            .addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
    }

    fun createProduct(activity: CreateProduct, product: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .add(product)
            .addOnSuccessListener { document ->
                if(product.id == "") {
                    product.id = document.id
                    activity.setProductID(document.id)
                }
                activity.productCreatedSuccessfully()
            }
            .addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
    }

    fun updateNoOfOrders(productID: String,orderNumber: Int) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productID)
            .update(Constants.PRODUCT_NO_OF_ORDERS,orderNumber)
            .addOnSuccessListener {
                Log.e("Order number set", orderNumber.toString())
            }
    }

    fun setFirebaseOrderID(activity: OrderConfirmation, id: String) {
        mFireStore.collection(Constants.ORDER)
            .document(id)
            .update("id",id)
            .addOnSuccessListener {
                Log.e("ID set", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("ID not set", "Error updating document", e) }
            }

    fun completeOrder(activity: OrderedProductDetails,status: String,id: String){
        mFireStore.collection(Constants.ORDER)
            .document(id)
            .update("complete",status)
            .addOnSuccessListener {
                activity.confirmationComplete()
                Log.e("Order Completed", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w("Order not Completed", "Error updating document", e) }
    }

    fun setFirebaseProductID(activity: Activity, id : String) {
        when(activity) {
            is CreateProduct -> {
                mFireStore.collection(Constants.PRODUCTS)
                    .document(id)
                    .update("id",id)
                    .addOnSuccessListener {
                        Log.e("ID set", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("ID not set", "Error updating document", e) }
            }
        }

    }
    fun userDetails(activity : Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
              val userDetails = document.toObject(User::class.java)!!
              when (activity) {
                  is MainActivity -> {
                    activity.updateNavigationUserDetails(userDetails)
                  }
                  is ProfileActivity -> {
                      activity.setDataToUI(userDetails)
                  }
                  is ProductDetailActivity -> {
                      activity.setUserDetails(userDetails)
                  }
              }
            }
            .addOnFailureListener {
                when(activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun farmerDetails(activity: Activity) {
        mFireStore.collection(Constants.FARMERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val farmerDetails = document.toObject(Farmer::class.java)!!
                when(activity) {
                    is CreateProduct -> {
                        activity.getFarmerDetails(farmerDetails)
                    }
                    is MainActivity -> {
                        activity.getFarmerDetails(farmerDetails)
                    }
                }
            }.addOnFailureListener {

            }
    }

    fun customerDetailsGivenID(customerId: String) : Customer{
        var customer: Customer? = null
        mFireStore.collection(Constants.CUSTOMERS)
            .document(customerId)
            .get()
            .addOnSuccessListener { document ->
                customer = if(document != null)
                    document.toObject(Customer::class.java)
                else
                    Customer()
            }.addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
        return customer!!
    }

    fun farmerDetailsGivenID(farmerId: String) : Farmer {
        lateinit var farmer: Farmer
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.ID,farmerId)
            .get()
            .addOnSuccessListener { document ->
                for (i in document)
                    farmer = i.toObject(Farmer::class.java)
            }.addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
        return farmer
    }

    fun customerDetails(activity: Activity) {
        mFireStore.collection(Constants.CUSTOMERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val customerDetails = document.toObject(Customer::class.java)!!
                when(activity) {
                    is MainActivity -> {
                        activity.getCustomerDetails(customerDetails)
                    }
                }

            }

    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                when(activity) {
                    is ProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when(activity) {
                    is ProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun updateProduct(activity: UpdateProduct,productHashMap: HashMap<String,Any>,productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .update(productHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Product Data updated successfully!")
                Toast.makeText(activity, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                activity.productUpdateSuccess()
            }
            .addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
    }

    fun deleteOrder(activity: Activity,orderID: String){
        mFireStore.collection(Constants.ORDER)
            .document(orderID)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                when(activity) {
                    is CustomerMyOrdersActivity -> {
                        activity.deleteProductSuccess()
                    }
                }
            }
            .addOnFailureListener {  e -> Log.w(TAG, "Error deleting document", e) }
    }

    fun deleteProduct(activity: ProductDetailActivity,productID: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productID)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "ProductDocumentSnapshot successfully deleted!")
                activity.deletionSuccess()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    fun deleteOrder(orderID: String) {
        mFireStore.collection(Constants.ORDER)
            .document(orderID)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "OrderDocumentSnapshot successfully deleted!")
            } .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    fun updateCustomer(activity: ProfileActivity, customerHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CUSTOMERS)
            .document(getCurrentUserId())
            .update(customerHashMap)
            .addOnSuccessListener {
                activity.customerUpdateSuccess()
            }
            .addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
    }
    fun updateFarmer(activity: ProfileActivity,farmerHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.FARMERS)
            .document(getCurrentUserId())
            .update(farmerHashMap)
            .addOnSuccessListener {
                activity.farmerUpdateSuccess()
            }
            .addOnFailureListener {
                Log.e(
                    "Didn't work",
                    "Error writing document",
                )
            }
    }

    fun getProductDetails(activity: Activity,productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { returnedDocument ->
                Log.e("Get product Query Success", returnedDocument.toString())
                val productDetails = returnedDocument.toObject(Product::class.java)
                when(activity) {
                    is ProductDetailActivity -> {
                        activity.getProductDetailsToApp(productDetails!!)
                    }
                    is UpdateProduct -> {
                        activity.setDataToUI(productDetails!!)
                    }
                    is OrderConfirmation -> {
                        activity.returnProductDetails(productDetails!!)
                    }
                }
            }
    }

    fun getProductList(activity: Activity,field: String,value : String) {
        Log.e("CurrentUser", value)
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(field, value)
            .get()
            .addOnSuccessListener { returnedDocument ->
                Log.e("Get product Query Success", returnedDocument.documents.toString())
                val productList: ArrayList<Product> = ArrayList()
                val cerealList: ArrayList<Product> = ArrayList()
                val meatList: ArrayList<Product> = ArrayList()
                val dairyList: ArrayList<Product> = ArrayList()
                val fvList: ArrayList<Product> = ArrayList()
                val otherList: ArrayList<Product> = ArrayList()
                for (i in returnedDocument.documents) {
                    val product = i.toObject(Product::class.java)!!
                    // get the list based on farmerID
                    if (field == Constants.FARMER_ID)
                        productList.add(product)
                    // get the list based on average location
                    if (field == Constants.AVERAGE_LOCATION || field == Constants.PRODUCT_CATEGORY) {
                        // get cereal list
                        if (product.productCategory == Constants.CEREALS)
                            cerealList.add(product)
                        // get meat products
                        if (product.productCategory == Constants.MEAT)
                            meatList.add(product)
                        // get dairy products
                        if (product.productCategory == Constants.DAIRY)
                            dairyList.add(product)
                        // get fv products
                        if (product.productCategory == Constants.GREENSANDVEGETABLES)
                            fvList.add(product)
                        // get other products
                        if (product.productCategory == Constants.OTHER)
                            otherList.add(product)
                    }
                }
                when (activity) {
                    is ProductListActivity -> {
                        activity.assignThisProductList(productList)
                    }
                    is CerealListActivity -> {
                        activity.assignThisCerealList(cerealList)
                    }
                    is DairyListActivity -> {
                        activity.assignThisDairyList(dairyList)
                    }
                    is FVListActivity -> {
                        activity.assignThisFreshGreensFruitsList(fvList)
                    }
                    is MeatListActivity -> {
                        activity.assignThisMeatList(meatList)
                    }
                    is OtherProductListActivity -> {
                        activity.assignThisOtherProductList(otherList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }


        fun getProductsOrdered(activity: Activity, field: String,value : String) {
            val customerProductHashMap = HashMap<String,OrderedProduct>()
            val farmerProductHashMap = HashMap<String,OrderedProduct>()
            val customerBoughtProductHashMap = HashMap<String,OrderedProduct>()
            val farmerSoldProductHashMap = HashMap<String,OrderedProduct>()
            mFireStore.collection(Constants.ORDER)
                .whereEqualTo(field,value)
                .get()
                .addOnSuccessListener { orders ->
                    for(i in orders) {
                        val order = i.toObject(Order::class.java)
                        mFireStore.collection(Constants.PRODUCTS)
                            .document(order.productID)
                            .get()
                            .addOnSuccessListener { product ->
                                val productObject = product.toObject(Product::class.java)!!

                                when(activity) {
                                    is CustomerMyOrdersActivity -> {
                                        if(order.complete == "false") {
                                            val orderedProduct = OrderedProduct(
                                                productObject.productName,
                                                productObject.productImage,
                                                order.id,
                                                order.totalPrice,
                                                order.quantity,
                                                "",
                                                order.farmerID,
                                                order.farmerName,
                                                productObject.uniqueConfirmationID)
                                            Log.e("Farmer ID", order.farmerID)
                                            customerProductHashMap[order.id] = orderedProduct
                                            activity.assignThisOrderList(customerProductHashMap)
                                        }
                                    }
                                    is FarmerPendingOrdersActivity -> {
                                        if(order.complete == "false") {
                                            val orderedProduct = OrderedProduct(
                                                productObject.productName,
                                                productObject.productImage,
                                                order.id,
                                                order.totalPrice,
                                                order.quantity,
                                                order.customerID,
                                                "",
                                                "",
                                                productObject.uniqueConfirmationID)
                                            farmerProductHashMap[order.id] = orderedProduct
                                            activity.assignThisOrderList(farmerProductHashMap)
                                        }
                                    }
                                    is CustomerBoughtProducts -> {
                                        if(order.complete == "true"){
                                            val orderedProduct = OrderedProduct(
                                                productObject.productName,
                                                productObject.productImage,
                                                order.id,
                                                order.totalPrice,
                                                order.quantity,
                                                "",
                                                order.farmerID,
                                                order.farmerName,
                                                productObject.uniqueConfirmationID)
                                            customerBoughtProductHashMap[order.id] = orderedProduct
                                            activity.assignThisOrderList(customerBoughtProductHashMap)
                                        }
                                    }
                                    is FarmerSoldProducts -> {
                                        if(order.complete == "true"){
                                            val orderedProduct = OrderedProduct(
                                                productObject.productName,
                                                productObject.productImage,
                                                order.id,
                                                order.totalPrice,
                                                order.quantity,
                                                order.customerID,
                                                "",
                                                "",
                                                productObject.uniqueConfirmationID)
                                            farmerSoldProductHashMap[order.id] = orderedProduct
                                            activity.assignThisOrderList(farmerSoldProductHashMap)
                                        }
                                    }
                                }

                            }
                    }
                }
                .addOnFailureListener {
                    Log.e(activity.javaClass.simpleName, "Error while creating a board.")
                }

        }
    }



