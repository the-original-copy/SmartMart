package com.smartherd.smartmart.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityFvlistBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class FVListActivity : BaseActivity() {
    lateinit var binding: ActivityFvlistBinding
    lateinit var averageLocation: String
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFvlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarFvListActivity,"w")
        if(intent.hasExtra(Constants.AVERAGE_LOCATION)){
            averageLocation = intent.getStringExtra(Constants.AVERAGE_LOCATION)!!
            updateDetails(averageLocation)
        } else {
            updateDetails(averageLocation)
        }
    }

    fun assignThisFreshGreensFruitsList(list: ArrayList<Product>) {
        hideProgressDialog()
        populateProductLists(list,binding.rvFvList,binding.tvNoFvCreated,this)
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

    fun updateDetails(averageLocation: String) {
        showProgressDialog("Fetching products...")
        if(averageLocation.isNotEmpty())
            FireBaseClass().getProductList(this,Constants.AVERAGE_LOCATION,averageLocation)
        else
            FireBaseClass().getProductList(this,Constants.PRODUCT_CATEGORY,Constants.GREENSANDVEGETABLES)
    }
}