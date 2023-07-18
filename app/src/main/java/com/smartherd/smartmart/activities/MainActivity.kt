package com.smartherd.smartmart.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.smartherd.smartmart.IntroActivity
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityMainBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.databinding.NavHeaderMainBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.Customer
import com.smartherd.smartmart.models.Farmer
import com.smartherd.smartmart.models.User
import com.smartherd.smartmart.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding : ActivityMainBinding
    lateinit var navViewBinding : NavHeaderMainBinding
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    lateinit var userName : String
    lateinit var userRole : String
    lateinit var averageLocation : String
    lateinit var mCustomer : Customer
    lateinit var mFarmer: Farmer
    var menu : Menu? = null

    companion object {
        const val MY_PROFILE_REQUEST_CODE = 11
        const val USER_DETAILS_REQUEST_CODE = 12
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        navViewBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        updateDetails()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()
        binding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
        binding.navView.setNavigationItemSelectedListener(this)
        menu = binding.navView.menu
        binding.appBarMain.contentMain.cvCereal.setOnClickListener {
            val intent = Intent(this,CerealListActivity::class.java)
            intent.putExtra(Constants.AVERAGE_LOCATION,averageLocation)
            startActivity(intent)
        }
        binding.appBarMain.contentMain.cvDairy.setOnClickListener {
            val intent = Intent(this,DairyListActivity::class.java)
            intent.putExtra(Constants.AVERAGE_LOCATION,averageLocation)
            startActivity(intent)
        }
        binding.appBarMain.contentMain.cvMeat.setOnClickListener {
            val intent = Intent(this,MeatListActivity::class.java)
            intent.putExtra(Constants.AVERAGE_LOCATION,averageLocation)
            startActivity(intent)
        }
        binding.appBarMain.contentMain.cvFruitsAndVegetables.setOnClickListener {
            val intent = Intent(this,FVListActivity::class.java)
            intent.putExtra(Constants.AVERAGE_LOCATION,averageLocation)
            startActivity(intent)
        }
        binding.appBarMain.contentMain.cvOther.setOnClickListener {
            val intent = Intent(this,OtherProductListActivity::class.java)
            intent.putExtra(Constants.AVERAGE_LOCATION,averageLocation)
            startActivity(intent)
        }

    }

    fun updateDetails() {
        showProgressDialog("Updating details...")
        FireBaseClass().userDetails(this@MainActivity)
    }

    fun getFarmerDetails(farmer: Farmer) {
        mFarmer = farmer
        averageLocation = mFarmer.average_location
    }

    fun getCustomerDetails(customer: Customer) {
        mCustomer = customer
        averageLocation = mCustomer.average_location
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

    private fun setUpActionBar() {
        setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        supportActionBar?.title = null
        binding.appBarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding.appBarMain.toolbarTitle.typeface = typeface
    }
    private fun toggleDrawer() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
            R.id.nav_my_profile -> {
                val intent = Intent(this,ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_my_products -> {
                startActivity(Intent(this,ProductListActivity::class.java))
            }
            R.id.nav_my_orders -> {
                val intent = Intent(this,CustomerMyOrdersActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_my_pending_orders -> {
                val intent = Intent(this,FarmerPendingOrdersActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_my_sold_products -> {
                val intent = Intent(this,FarmerSoldProducts::class.java)
                startActivity(intent)
            }
            R.id.nav_my_history -> {
                val intent = Intent(this,CustomerBoughtProducts::class.java)
                startActivity(intent)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user : User) {
        hideProgressDialog()
        userName = user.name
        userRole = user.role
        navViewBinding.ivUserImage.let{
            Glide
                .with(this@MainActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(it)
        }
        navViewBinding.tvUsername.text = user.name
        if(user.role == "C"){
            navViewBinding.tvAccountType.text = "Customer"
            navViewBinding.tvAccountID.text = "CustomerID: +${user.id}"
            FireBaseClass().customerDetails(this)
            menu!!.findItem(R.id.nav_my_products).isVisible = false
            menu!!.findItem(R.id.nav_my_pending_orders).isVisible = false
            menu!!.findItem(R.id.nav_my_sold_products).isVisible = false
        }
        else if(user.role == "F"){
            navViewBinding.tvAccountType.text = "Farmer"
            FireBaseClass().farmerDetails(this)
            menu!!.findItem(R.id.nav_my_orders).isVisible = false
            menu!!.findItem(R.id.nav_my_history).isVisible = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FireBaseClass().userDetails(this)
        }
    }
}