package com.smartherd.smartmart.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityProfileBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.User
import com.smartherd.smartmart.utils.Constants
import com.smartherd.smartmart.utils.GetAddressFromLatLng
import java.io.IOException

class ProfileActivity : BaseActivity() {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }
    lateinit var binding: ActivityProfileBinding
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    // URI for selected image
    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageURL: String = ""
    private lateinit var mUserDetails: User
    private var mLatitude : Double = 0.0
    private var mLongitude: Double = 0.0
    private var mLocation : String = " "
    private var mPostalCode : String = " "
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpActionBar()
        updateUserDetails()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = resources.getColor(R.color.colorPrimary);
        }

        binding.ivUserImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE)
            }
        }
        binding.btnUpdate.setOnClickListener {
            if(mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                updateUserProfileData()
            }
        }

        binding.etLocation.setOnClickListener {
            Toast.makeText(
                this,
                "Click Select current location to set location",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.tvSelectCurrentLocation.setOnClickListener {
                requestNewLocationData()
        }
//        binding.etMobile.setOnClickListener {
//            val intent =Intent(Intent.ACTION_DIAL)
//            intent.data = Uri.parse("tel:" + mUserDetails.mobile)
//            startActivity(intent)
//        }
    }

    fun updateUserDetails() {
        showProgressDialog("Fetching data...")
        FireBaseClass().userDetails(this)
    }

    private fun showProgressDialog(text: String) {
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

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_white_ios_24)
            actionBar.title = "My Profile"
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setDataToUI(user: User) {
        hideProgressDialog()
        mUserDetails = user
        binding.ivUserImage.let{
            Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(it)
        }
        binding.etName.setText(mUserDetails.name)
        binding.etEmail.setText(mUserDetails.email)
        binding.etMobile.setText(mUserDetails.mobile)
        binding.etLocation.setText(mUserDetails.location)
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
    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null) {
            mSelectedImageFileUri = data.data
            try{
                binding.ivUserImage.let{
                    Glide
                        .with(this)
                        .load(Uri.parse(mSelectedImageFileUri.toString()))
                        .centerCrop()
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .into(it)
                }
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun uploadUserImage() {
        if(mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("USER_PROFILE_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                Log.e("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                    // get downloadable url to be stored in database
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        mProfileImageURL = uri.toString()
                        updateUserProfileData()
                    }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this@ProfileActivity,
                                exception.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
        }
    }

    private fun updateUserProfileData() {
        showProgressDialog("Updating...")
        val userHashMap = HashMap<String,Any>()
        val customerHashMap = HashMap<String,Any>()
        val farmerHashMap = HashMap<String,Any>()
        var anyChanges = false
        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChanges = true
        }
        if(binding.etName.text.toString() != mUserDetails.name){
            if(mUserDetails.role == "C")
                customerHashMap[Constants.NAME] = binding.etName.text.toString()
            else if(mUserDetails.role == "F")
                farmerHashMap[Constants.NAME] = binding.etName.text.toString()
            userHashMap[Constants.NAME] = binding.etName.text.toString()
            anyChanges = true
        }
        if(binding.etMobile.text.toString() != mUserDetails.mobile){
            if(mUserDetails.role == "C")
                customerHashMap[Constants.MOBILE] = binding.etMobile.text.toString()
            else if(mUserDetails.role == "F")
                farmerHashMap[Constants.MOBILE] = binding.etMobile.text.toString()
            userHashMap[Constants.MOBILE] = binding.etMobile.text.toString()
            anyChanges = true
        }
        if(binding.etLocation.text.toString() != mUserDetails.location){
            if(mUserDetails.role=="C"){
                customerHashMap[Constants.LOCATION] = mLocation
                customerHashMap[Constants.LONGITUDE] = mLongitude
                customerHashMap[Constants.LATITUDE] = mLatitude
                customerHashMap[Constants.AVERAGE_LOCATION] = mPostalCode
            }
            else if(mUserDetails.role == "F"){
                farmerHashMap[Constants.LOCATION] = mLocation
                farmerHashMap[Constants.LONGITUDE] = mLongitude
                farmerHashMap[Constants.LATITUDE] = mLatitude
                farmerHashMap[Constants.AVERAGE_LOCATION] = mPostalCode
            }
            userHashMap[Constants.LOCATION] = mLocation
            anyChanges = true
        }
        if(anyChanges && mUserDetails.role == "C"){
            FireBaseClass().updateCustomer(this,customerHashMap)
            FireBaseClass().updateUserProfileData(this,userHashMap)
        }
        if(anyChanges && mUserDetails.role == "F"){
            FireBaseClass().updateFarmer(this,farmerHashMap)
            FireBaseClass().updateUserProfileData(this,userHashMap)
        }
    }

    private val mLocationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val mLastLocation: Location? = locationResult.lastLocation
            mLatitude = mLastLocation!!.latitude
            Log.e("Current Latitude", "$mLatitude")
            mLongitude = mLastLocation.longitude
            Log.e("Current Longitude", "$mLongitude")
            val addressTask = GetAddressFromLatLng(this@ProfileActivity,mLatitude,mLongitude)
            addressTask.setAddressListener(object: GetAddressFromLatLng.AddressListener{
                override fun onAddressFound(address: String?) {
                    Log.e("Address ::", "" + address)
                    mLocation = address!!
                    mPostalCode = address.substringBefore(",")
                    Log.e("Postal Code::",mPostalCode)
                    binding.etLocation.setText(address)
                }

                override fun onError() {
                    Log.e("Get Address ::", "Something is wrong...")
                }
            })
            addressTask.getAddress()
        }
    }

    //Location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun customerUpdateSuccess() {
        Log.e("Customer Updated Successfully", "Customer Data updated successfully!")
        Toast.makeText(this, "Customer updated successfully!", Toast.LENGTH_SHORT).show()
    }
    fun farmerUpdateSuccess() {
        Log.e("Farmer Updated Successfully", "Fammer Data updated successfully!")
        Toast.makeText(this, "Farmer updated successfully!", Toast.LENGTH_SHORT).show()
    }
}