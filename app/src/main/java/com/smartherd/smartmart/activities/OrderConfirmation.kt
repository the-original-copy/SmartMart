package com.smartherd.smartmart.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityOrderConfirmationBinding
import com.smartherd.smartmart.utils.Constants

class OrderConfirmation : BaseActivity() {
    lateinit var binding: ActivityOrderConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarMyOrderActivity,"w")
        if(intent.hasExtra(Constants.ID) && intent.hasExtra(Constants.INTENT_USER_ID)){
            val customerID: String = intent.getStringExtra(Constants.INTENT_USER_ID)!!
            val productID: String = intent.getStringExtra(Constants.ID)!!
            Log.e("Customer ID",customerID)
            Log.e("Product ID",productID)
        }
    }
}