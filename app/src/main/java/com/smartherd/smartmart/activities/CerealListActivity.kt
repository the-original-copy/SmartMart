package com.smartherd.smartmart.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.smartherd.smartmart.databinding.ActivityCerealListBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class CerealListActivity : BaseActivity() {
    lateinit var binding: ActivityCerealListBinding
    lateinit var averageLocation: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCerealListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarCerealListActivity,"w")
        if(intent.hasExtra(Constants.AVERAGE_LOCATION)){
            averageLocation = intent.getStringExtra(Constants.AVERAGE_LOCATION)!!
            FireBaseClass().getProductList(this,Constants.AVERAGE_LOCATION,averageLocation)
        }
    }

    fun assignThisCerealList(list: ArrayList<Product>) {
        populateProductLists(list,binding.rvCerealList,binding.tvNoDairyCreated,this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == ProductListActivity.CREATE_PRODUCT_REQUEST_CODE) {
            FireBaseClass().getProductList(this,Constants.AVERAGE_LOCATION,averageLocation)
        }
    }
}