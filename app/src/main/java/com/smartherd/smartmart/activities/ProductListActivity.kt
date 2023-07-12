package com.smartherd.smartmart.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityProductListBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class ProductListActivity : BaseActivity() {
    lateinit var binding: ActivityProductListBinding
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
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
        updateDetails()
        binding.fabCreateProduct.setOnClickListener {
            startActivityForResult(Intent(this,CreateProduct::class.java),
                CREATE_PRODUCT_REQUEST_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == CREATE_PRODUCT_REQUEST_CODE) {
            updateDetails()
        }
    }

    fun assignThisProductList(list: ArrayList<Product>) {
        hideProgressDialog()
        populateProductLists(list,binding.rvProductList,binding.tvNoProductCreated,this)
    }

    fun updateDetails() {
        showProgressDialog("Fetching products...")
        FireBaseClass().getProductList(this,Constants.FARMER_ID,getCurrentUserId())
    }

}