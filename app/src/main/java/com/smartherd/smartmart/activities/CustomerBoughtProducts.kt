package com.smartherd.smartmart.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityCustomerBoughtProductsBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.OrderedProduct
import com.smartherd.smartmart.utils.Constants

class CustomerBoughtProducts : BaseActivity() {
    lateinit var binding: ActivityCustomerBoughtProductsBinding
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBoughtProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        getBoughtProducts()
        setActionBar(binding.toolbarProductListActivity,"w")
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

    fun getBoughtProducts(){
        showProgressDialog("Fetching products....")
        FireBaseClass().getProductsOrdered(this, Constants.CUSTOMER_ID,getCurrentUserId())
    }

    fun assignThisOrderList(hashMap: HashMap<String, OrderedProduct>){
        hideProgressDialog()
        val orderedProducts: ArrayList<OrderedProduct> = ArrayList(hashMap.values)
        populateOrderLists(orderedProducts,binding.rvBoughtProductList,binding.tvNoProductCreated,this)
    }
}