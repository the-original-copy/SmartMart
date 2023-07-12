package com.smartherd.smartmart.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityProductListBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class ProductListActivity : BaseActivity() {
    lateinit var binding: ActivityProductListBinding
    companion object {
        open val CREATE_PRODUCT_REQUEST_CODE = 5
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarProductListActivity,"w")
        FireBaseClass().getProductList(this,Constants.FARMER_ID,getCurrentUserId())
        binding.fabCreateProduct.setOnClickListener {
            startActivityForResult(Intent(this,CreateProduct::class.java),
                CREATE_PRODUCT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == CREATE_PRODUCT_REQUEST_CODE) {
            FireBaseClass().getProductList(this,Constants.FARMER_ID,getCurrentUserId())
        }
    }

    fun assignThisProductList(list: ArrayList<Product>) {
        populateProductLists(list,binding.rvProductList,binding.tvNoProductCreated,this)
    }

}