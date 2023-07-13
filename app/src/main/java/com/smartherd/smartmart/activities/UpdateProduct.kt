package com.smartherd.smartmart.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityUpdateProductBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.dialogs.LabelCategoryDialog
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Farmer
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants

class UpdateProduct : BaseActivity() {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 6
        private const val PICK_IMAGE_REQUEST_CODE = 7
        private const val CREATE_PRODUCT_REQUEST_CODE = 8
    }
    lateinit var binding: ActivityUpdateProductBinding
    private var selectedCategory: String = " "
    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    lateinit var mFarmerDetails: Farmer
    lateinit var mProduct: Product
    lateinit var productID : String
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar(binding.toolbarUpdateProductActivity,"w")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if(intent.hasExtra(Constants.ID)){
            productID = intent.getStringExtra(Constants.ID)!!
            getProductData(productID)
            binding.btnUpdate.setOnClickListener {
                if(mSelectedImageFileUri != null) {
                    uploadProductImage(productID)
                } else {
                    updateProduct(productID)
                }
            }
        }
        binding.etProductCategory.setOnClickListener {
            labelCategoriesListDialog()
        }
        binding.ivProductImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    private fun setCategory(category: String) {
        binding.etProductCategory.setText(category)
    }
    private fun labelCategoriesListDialog() {
        val listDialog = object : LabelCategoryDialog(this) {
            override fun onCategorySelected(category: String) {
                selectedCategory = category
                setCategory(selectedCategory)
            }
        }
        listDialog.show()
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

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun uploadProductImage(productID: String){
        if(mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("PRODUCT_IMAGE" + getFileExtension(mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapShot ->
                    Log.e("Firebase Image URL",taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                    taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        mProductImageURL = uri.toString()
                        // Update Product
                        updateProduct(productID)
                    }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this@UpdateProduct,
                                exception.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null) {
            mSelectedImageFileUri = data.data
            binding.ivProductImage.setImageURI(mSelectedImageFileUri)
        }
    }

    fun setDataToUI(product: Product) {
        hideProgressDialog()
        mProduct = product
        binding.toolbarUpdateProductActivity.title = mProduct.productName
        binding.ivProductImage.let{
            Glide.with(this)
                .load(mProduct.productImage)
                .fitCenter()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(it)
        }
        binding.etProductName.setText(mProduct.productName)
        binding.etProductDescription.setText(mProduct.productDescription)
        binding.etProductCategory.setText(mProduct.productCategory)
        binding.etProductPrice.setText(mProduct.productPrice)
    }

    fun updateProduct(productID: String) {
        showProgressDialog("Updating...")
        val productHashMap = HashMap<String,Any>()
        var anyChanges = false
        if(mProductImageURL.isNotEmpty() && mProductImageURL != mProduct.productImage) {
            productHashMap[Constants.PRODUCT_IMAGE] = mProductImageURL
            anyChanges = true
        }
        if(binding.etProductName.text.toString() != mProduct.productName) {
            productHashMap[Constants.PRODUCT_NAME] = binding.etProductName.text.toString()
            anyChanges = true
        }
        if(binding.etProductDescription.text.toString() != mProduct.productDescription) {
            productHashMap[Constants.PRODUCT_DESCRIPTION] = binding.etProductDescription.text.toString()
            anyChanges = true
        }
        if(binding.etProductCategory.text.toString() != mProduct.productDescription) {
            productHashMap[Constants.PRODUCT_CATEGORY] = binding.etProductCategory.text.toString()
            anyChanges = true
        }
        if(binding.etProductPrice.text.toString() != mProduct.productPrice.toString()) {
            productHashMap[Constants.PRODUCT_PRICE] = binding.etProductPrice.text.toString()
            anyChanges = true
        }
        if(anyChanges) {
            FireBaseClass().updateProduct(this,productHashMap,productID)
        }
    }

    fun getProductData(productID : String){
        showProgressDialog("Fetching product data")
        FireBaseClass().getProductDetails(this,productID)
    }

    fun productUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}