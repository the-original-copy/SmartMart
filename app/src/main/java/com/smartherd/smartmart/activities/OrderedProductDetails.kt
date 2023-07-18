package com.smartherd.smartmart.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityOrderedProductDetailsBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.utils.Constants

class OrderedProductDetails : BaseActivity() {
    lateinit var binding: ActivityOrderedProductDetailsBinding
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    lateinit var orderID: String
    var farmerID: Int = 0
    lateinit var appFarmerID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderedProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarMyOrderdProductActivity,"w")
        binding.toolbarMyOrderdProductActivity.title = "Confirm Product Pickup"
        if(intent.hasExtra(Constants.ORDER_ID_WITHIN_APP) && intent.hasExtra(Constants.FARMER_ID_WITHIN_APP)){
            Log.e("Received",intent.getIntExtra(Constants.FARMER_ID_WITHIN_APP,0).toString())
            farmerID = intent.getIntExtra(Constants.FARMER_ID_WITHIN_APP,0)
            orderID = intent.getStringExtra(Constants.ORDER_ID_WITHIN_APP)!!
        }
        appFarmerID = binding.etFarmerID.text.toString()
            binding.btnConfirmOrder.setOnClickListener {
                Log.e("Entered Unique ID",binding.etFarmerID.text.toString())
                Log.e("Product Unique ID",farmerID.toString())
                if(binding.etFarmerID.text.toString() == farmerID.toString())
                {
                showErrorSnackBar("Right farmer ID set")
                confirmOrder()
                } else {
                showErrorSnackBar("Wrong farmer ID set")
                }}
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

    fun confirmOrder(){
        showProgressDialog("Confirming Pickup....")
        FireBaseClass().completeOrder(this,"true",orderID)
    }

    fun confirmationComplete() {
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
    }
}