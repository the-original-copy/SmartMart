package com.smartherd.smartmart.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityDairyListBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class DairyListActivity : BaseActivity() {
    lateinit var binding: ActivityDairyListBinding
    lateinit var averageLocation: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDairyListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarDairyListActivity,"w")
        if(intent.hasExtra(Constants.AVERAGE_LOCATION)){
            averageLocation = intent.getStringExtra(Constants.AVERAGE_LOCATION)!!
            FireBaseClass().getProductList(this,Constants.AVERAGE_LOCATION,averageLocation)
        } else {
            FireBaseClass().getProductList(this,Constants.PRODUCT_CATEGORY,Constants.MEAT)
        }
    }

    fun assignThisDairyList(list: ArrayList<Product>) {
        populateProductLists(list,binding.rvDairyList,binding.tvNoDairyCreated,this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == ProductListActivity.CREATE_PRODUCT_REQUEST_CODE) {
            FireBaseClass().getProductList(this, Constants.AVERAGE_LOCATION,averageLocation)
        }
    }
}