package com.smartherd.smartmart.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityUserSignUpBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Customer
import com.smartherd.smartmart.models.User
import com.smartherd.smartmart.utils.GetAddressFromLatLng
import java.util.regex.Pattern

open class UserSignUp : BaseActivity() {
    lateinit var binding : ActivityUserSignUpBinding
    private lateinit var auth : FirebaseAuth
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    private var mLatitude : Double = 0.0
    private var mLongitude: Double = 0.0
    private var mLocation : String = " "
    private var mPostalCode : String = " "
    // Location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        // Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.tvFarmerSignup.setOnClickListener {
            startActivity(Intent(this@UserSignUp,FarmerSignUp::class.java))
            finish()
        }
        binding.btnSignUp.setOnClickListener{
            registerUser()
        }
        binding.etLocation.setOnClickListener{
            Toast.makeText(
                this,
                "Click Select current location to set location",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.tvSelectCurrentLocation.setOnClickListener{
            if(!isLocationEnabled()) {
                Toast.makeText(
                    this,
                    "Your location provider is turned off. Please turn it on.",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            } else {
                Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(object: MultiplePermissionsListener{
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if(report!!.areAllPermissionsGranted()){
                               requestNewLocationData()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                        ) {
                            showRationaleDialogForPermissions()
                        }
                    }).onSameThread().check()
            }
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

    fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this@UserSignUp)
            .setMessage("It looks like you have rejected the permissions required for this feature." +
                    "It can be enabled under applications settings")
            .setPositiveButton("GO TO SETTINGS") {
                _,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                } catch(e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") {
                dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateForm(name : String,email : String,password : String, phoneNumber : String) : Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showErrorSnackBar("Enter right email format")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password")
                false
            }
            !isValidPassword(password) -> {
                showErrorSnackBar("A password must have a minimum of 8 characters")
                false
            }
            TextUtils.isEmpty(phoneNumber) -> {
                showErrorSnackBar("Please enter phone number")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerUser() {
        val name : String = binding.etName.text.toString().trim {it <= ' '}
        val email : String = binding.etEmail.text.toString().trim {it <= ' '}
        val password : String = binding.etPassword.text.toString().trim{ it <= ' '}
        val phonenumber : String = binding.etPhoneNumber.text.toString().trim {it <= ' '}
        val localAreaName: String = binding.etLocalAreaName.text.toString().trim(){it <= ' '}
        if(validateForm(name,email,password,phonenumber)) {
            showProgressDialog("Registering user...")
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->

                if(task.isSuccessful) {
                    val firebaseUser : FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid,name,registeredEmail," ",phonenumber,mLocation,"C",localAreaName)
                    val customer = Customer(firebaseUser.uid,name,registeredEmail,phonenumber,mLocation,mLongitude,mLatitude,mPostalCode,localAreaName)
                    Log.e("FireStore", user.id)
                    FireBaseClass().registerUser(this@UserSignUp,user)
                    FireBaseClass().createCustomer(this@UserSignUp,customer)
                } else {
                    Toast.makeText(
                        this@UserSignUp,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    open fun isValidPassword(password : String) : Boolean {
        val PASSWORD_PATTERN : Pattern = Pattern.compile("[a-zA-Z0-9\\\\!\\\\@\\\\#\\\\\$]{8,24}")
        return PASSWORD_PATTERN.matcher(password).matches()
    }

    // Location
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //Location
    private val mLocationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val mLastLocation: Location? = locationResult.lastLocation
            mLatitude = mLastLocation!!.latitude
            Log.e("Current Latitude", "$mLatitude")
            mLongitude = mLastLocation.longitude
            Log.e("Current Longitude", "$mLongitude")


            // Get address
            val addressTask = GetAddressFromLatLng(this@UserSignUp,mLatitude,mLongitude)
            addressTask.setAddressListener(object: GetAddressFromLatLng.AddressListener {
                override fun onAddressFound(address: String?) {
                    Log.e("Address ::", "" + address)
                    mLocation = address!!
                    val averageLocation = address.split(',')[1]
                    mPostalCode = averageLocation
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
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,Looper.myLooper())
    }

    fun userRegisteredSuccess() {
        hideProgressDialog()
        // hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    fun customerCreated() {
        Toast.makeText(
            this@UserSignUp,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()
    }

}