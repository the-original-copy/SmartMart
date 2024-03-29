package com.smartherd.smartmart.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityProductDetailBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Farmer
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.models.User
import com.smartherd.smartmart.utils.Constants
import java.net.URL
import java.util.*

class ProductDetailActivity : BaseActivity() {
    lateinit var binding: ActivityProductDetailBinding
    lateinit var mProduct: Product
    lateinit var productID: String
    lateinit var mUser: User
    lateinit var mUserRole: String
    lateinit var mFarmerName: String
    lateinit var mCustomerName : String
    var quantity: Int = 0
    var mLongitude: Double? = null
    var mLatitude: Double? = null
    lateinit var mSelectedImageURL: URL
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    var mFarmer: Farmer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateUserDetails()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarProductDetail,"w")
        if(intent.hasExtra(Constants.ID)){
            productID = intent.getStringExtra(Constants.ID)!!
            updateProductData(productID)
            binding.btnUpdateProduct.setOnClickListener {
                val intent = Intent(this,UpdateProduct::class.java)
                intent.putExtra(Constants.ID,productID)
                startActivity(intent)
            }
            binding.btnDeleteProduct.setOnClickListener {
                deleteProduct(productID)
            }

            // Open view on map activity\
            binding.tvProductLocation.setOnClickListener {
                mLatitude = mProduct.latitude
                mLongitude = mProduct.longitude
                val uri: String = java.lang.String.format(
                    Locale.ENGLISH,
                    "http://maps.google.com/maps?daddr=%f,%f",
                    mLatitude,
                    mLongitude
                )
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }

            binding.ivAdd.setOnClickListener {
                quantity += 1
                binding.tvProductQuantity.text = quantity.toString()
            }
            binding.ivLess.setOnClickListener {
                quantity -= 1
                binding.tvProductQuantity.text = quantity.toString()
            }

            binding.btnOrder.setOnClickListener {
                val intent = Intent(this,OrderConfirmation::class.java)
                intent.putExtra(Constants.ID,productID)
                Log.e("Product ID passed",productID)
                intent.putExtra(Constants.INTENT_USER_ID,mUser.id)
                Log.e("User ID passed",mUser.id)
                intent.putExtra(Constants.QUANTITY,quantity)
                intent.putExtra(Constants.CUSTOMER_NAME_WITHIN_APP,mCustomerName)
                startActivity(intent)
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

    fun setUserDetails(user: User) {
        hideProgressDialog()
        mUser = user
        mUserRole = user.role
        // Pass the name of the customer to an order from here then from order confirmation to order
        mCustomerName = mUser.name
        if(mUserRole == "C") {
            binding.btnDeleteProduct.visibility = View.INVISIBLE
            binding.btnUpdateProduct.visibility = View.INVISIBLE
            binding.btnOrder.visibility = View.VISIBLE
            binding.setQuantity.visibility = View.VISIBLE
        }
        else if(mUserRole == "F") {
            binding.btnDeleteProduct.visibility = View.VISIBLE
            binding.btnUpdateProduct.visibility = View.VISIBLE
            binding.btnOrder.visibility = View.INVISIBLE
            binding.setQuantity.visibility = View.INVISIBLE
        }
    }

    fun getProductDetailsToApp(product: Product) {
        hideProgressDialog()
        mProduct = product
        binding.toolbarProductDetail.title = product.productName
        binding.toolbarProductDetail.setTitleTextColor(resources.getColor(R.color.white))
        binding.ivProductImage.let {
            Glide
                .with(this)
                .load(product.productImage)
                .centerCrop()
                .placeholder(R.drawable.detail_screen_image_placeholder)
                .into(it)
        }
        binding.tvProductName.text = product.productName
        binding.tvProductDescription.text = product.productDescription
        binding.tvProductCategory.text = product.productCategory
        binding.tvProductFarmer.text = product.farmerName
        binding.tvProductPrice.text = product.productPrice.toString()
        binding.tvProductLocation.text = product.location


    }

    fun updateUserDetails() {
        // showProgressDialog("Updating View...")
        FireBaseClass().userDetails(this)
    }
    fun updateProductData(productId: String) {
        showProgressDialog("Fetching product data...")
        FireBaseClass().getProductDetails(this,productId)

    }
    fun getFarmerDetails(farmer: Farmer){
        mFarmer = farmer
        binding.tvProductName.text = mFarmer!!.name
    }

    fun deleteProduct(productId: String){
        showProgressDialog("Deleting product.....")
        FireBaseClass().deleteProduct(this@ProductDetailActivity,productId)
    }

    fun deletionSuccess() {
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        setResult(Activity.RESULT_OK)
        finish()
    }

}