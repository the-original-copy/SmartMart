package com.smartherd.smartmart.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityMeatListBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class MeatListActivity : BaseActivity() {
    lateinit var binding: ActivityMeatListBinding
    lateinit var averageLocation: String
    var localAreaName: String = ""
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarMeatListActivity,"w")
        if(intent.hasExtra(Constants.LOCAL_AREA_NAME)){
            Log.e("Main Activity put extra tag ", Constants.LOCAL_AREA_NAME)
            Log.e("Local area name has extra", localAreaName)
            localAreaName = intent.getStringExtra(Constants.LOCAL_AREA_NAME)!!
            Log.e("Cereal Local name received", localAreaName)
            updateDetails(localAreaName)
        }
        else {
            if(intent.hasExtra(Constants.AVERAGE_LOCATION)){
                averageLocation = intent.getStringExtra(Constants.AVERAGE_LOCATION)!!
                updateDetails(averageLocation)
            }else {
                updateDetails(averageLocation)
            }
        }
    }

    fun assignThisMeatList(list: ArrayList<Product>) {
        hideProgressDialog()
        populateProductLists(list,binding.rvMeatList,binding.tvNoMeatCreated,this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == ProductListActivity.CREATE_PRODUCT_REQUEST_CODE) {
            updateDetails(averageLocation)
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

    fun updateDetails(locationValue: String) {
        showProgressDialog("Fetching products...")
        if(locationValue.isNotEmpty()) {
            if(locationValue == localAreaName)
                FireBaseClass().getProductList(this,Constants.LOCAL_AREA_NAME,locationValue)
            else
                FireBaseClass().getProductList(this,Constants.AVERAGE_LOCATION,locationValue)
        }
        else
            FireBaseClass().getProductList(this,Constants.PRODUCT_CATEGORY,Constants.MEAT)
    }
}