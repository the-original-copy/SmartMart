package com.smartherd.smartmart.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityOrderConfirmationBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Order
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class OrderConfirmation : BaseActivity() {
    lateinit var binding: ActivityOrderConfirmationBinding
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    lateinit var mProduct: Product
    private var mProductID: String = ""
    private var mCustomerID: String = ""
    private var mFarmerID: String = ""
    private var mProductPrice: Int = 0
    private var mQuantity: Int = 0
    private var mTotalPrice: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarMyOrderActivity,"w")
        binding.toolbarMyOrderActivity.title = "Confirm Order"
        if(intent.hasExtra(Constants.ID) && intent.hasExtra(Constants.INTENT_USER_ID)){
            mCustomerID = intent.getStringExtra(Constants.INTENT_USER_ID)!!
            mProductID = intent.getStringExtra(Constants.ID)!!
            mQuantity = intent.getIntExtra(Constants.QUANTITY,0)
            getProductDetails(mProductID)

            binding.btnConfirmOrder.setOnClickListener {
                orderProduct()
            }
        }
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

    fun orderProduct() {
        showProgressDialog("Finalizing Order...")
        val order = Order(
            "",
            mProductID,
            mCustomerID,
            mProduct.farmerID,
            mQuantity,
            mTotalPrice
        )
        FireBaseClass().createOrder(this,order)
        mProduct.productNumberOfOrders+=1
        FireBaseClass().updateNoOfOrders(mProductID,mProduct.productNumberOfOrders)
    }

    fun getProductDetails(productID: String) {
        showProgressDialog("Fetching details...")
        FireBaseClass().getProductDetails(this,productID)
    }

    fun returnProductDetails(product: Product) {
        hideProgressDialog()
        mProduct = product
        binding.ivOrderImage.let {
            Glide
                .with(this)
                .load(product.productImage)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(it)
        }
        binding.etProductName.setText(product.productName)
        binding.etProductDescription.setText(product.productDescription)
        binding.etQuantity.setText(mQuantity.toString())
        binding.etProductPrice.setText(product.productPrice.toString())
        val total = product.productPrice * mQuantity
        mTotalPrice = total
        binding.totalAmountPayable.text = total.toString()
        mProductPrice = product.productPrice
    }

    fun setOrderID(orderID: String) {
        FireBaseClass().setFirebaseOrderID(this,orderID)
    }

    fun orderCreatedSuccessfully() {
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}