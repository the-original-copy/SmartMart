package com.smartherd.smartmart.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityProductDetailBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Farmer
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.models.User
import com.smartherd.smartmart.utils.Constants
import java.net.URL

class ProductDetailActivity : BaseActivity() {
    lateinit var binding: ActivityProductDetailBinding
    lateinit var mProduct: Product
    lateinit var mUser: User
    lateinit var mUserRole: String
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
            val productId = intent.getStringExtra(Constants.ID)
            updateProductData(productId!!)
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
        if(mUserRole == "C") {
            binding.btnDeleteProduct.visibility = View.INVISIBLE
            binding.btnUpdateProduct.visibility = View.INVISIBLE
            binding.btnOrder.visibility = View.VISIBLE
        }
        else if(mUserRole == "F") {
            binding.btnDeleteProduct.visibility = View.VISIBLE
            binding.btnUpdateProduct.visibility = View.VISIBLE
            binding.btnOrder.visibility = View.INVISIBLE
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
                .fitCenter()
                .placeholder(R.drawable.detail_screen_image_placeholder)
                .into(it)
        }
        binding.tvProductName.text = product.productName
        binding.tvProductDescription.text = product.productDescription
        FireBaseClass().farmerDetailsGivenID(this,product.farmerID)
        binding.tvProductPrice.text = product.productPrice


    }

    fun updateUserDetails() {
        showProgressDialog("Updating View...")
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

}