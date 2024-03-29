package com.smartherd.smartmart.activities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartherd.smartmart.adapter.OrdersListAdapter
import com.smartherd.smartmart.databinding.ActivityCustomerMyOrdersBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.OrderedProduct
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class CustomerMyOrdersActivity : BaseActivity() {
    lateinit var binding: ActivityCustomerMyOrdersBinding
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarMyOrdersListActivity,"w")
        getOrderedProducts()
    }

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

    fun getOrderedProducts() {
        showProgressDialog("Fetching products....")
        FireBaseClass().getProductsOrdered(this,Constants.CUSTOMER_ID,getCurrentUserId())
    }

    fun assignThisOrderList(hashMap: HashMap<String,OrderedProduct>) {
        hideProgressDialog()
        val orderedProducts: ArrayList<OrderedProduct> = ArrayList(hashMap.values)
        populateOrderLists(orderedProducts,binding.rvOrdersList,binding.tvNoOrderCreated,this)
    }

    fun deleteProductSuccess() {
        getOrderedProducts()
    }
}