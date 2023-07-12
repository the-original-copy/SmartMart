package com.smartherd.smartmart.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.smartherd.smartmart.databinding.ActivityCreateProductBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.dialogs.LabelCategoryDialog
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Farmer
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants
import java.io.IOException

class CreateProduct : BaseActivity() {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 3
        private const val PICK_IMAGE_REQUEST_CODE = 4
        private const val CREATE_PRODUCT_REQUEST_CODE = 5
    }
    lateinit var binding: ActivityCreateProductBinding
    private var selectedCategory: String = " "
    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    lateinit var mFarmerDetails: Farmer
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar(binding.toolbarCreateProductActivity,"w")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        FireBaseClass().farmerDetails(this)
        binding.etProductCategory.setOnClickListener {
            labelCategoriesListDialog()
        }

        binding.tvAddImage.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_CODE)
            }
        }
        binding.btnCreateProduct.setOnClickListener {
            if(mSelectedImageFileUri != null)
                uploadProductImage()
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

    fun getFarmerDetails(farmer: Farmer) {
        mFarmerDetails = farmer
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

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST_CODE)
    }

    private fun uploadProductImage(){
        if(mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("PRODUCT_IMAGE" + getFileExtension(mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapShot ->
                    Log.e("Firebase Image URL",taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                    taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        mProductImageURL = uri.toString()
                        createProduct()
                    }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this@CreateProduct,
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

    fun createProduct() {
        showProgressDialog("Creating product...")
        val product = Product(
            "",
            binding.etProductName.text.toString(),
            binding.etProductDescription.text.toString(),
            binding.etProductCategory.text.toString(),
            binding.etProductPrice.text.toString(),
            mProductImageURL,
            mFarmerDetails.id,
            mFarmerDetails.longitude,
            mFarmerDetails.latitude,
            mFarmerDetails.average_location
        )
        FireBaseClass().createProduct(this,product)
    }

    fun setProductID(productId: String) {
        FireBaseClass().setFirebaseProductID(this,productId)
    }

    fun productCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}