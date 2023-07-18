package com.smartherd.smartmart.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.smartherd.smartmart.R
import com.smartherd.smartmart.adapter.OrdersListAdapter
import com.smartherd.smartmart.adapter.ProductListAdapter
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.OrderedProduct
import com.smartherd.smartmart.models.Product
import com.smartherd.smartmart.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

open class BaseActivity : AppCompatActivity() {
    //lateinit var basebinding : DialogProgressBinding
    // If user presses back button more than once the app will close
    private var doubleBackToExitPressedOnce = false
    //private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

//    fun showProgressDialog(text: String) {
//        mProgressDialog = Dialog(this)
//        basebinding = DialogProgressBinding.inflate(layoutInflater)
//        basebinding.root.let {
//            mProgressDialog.setContentView(it)
//        }
//        basebinding.tvProgressText.text = text
//        mProgressDialog.show()
//    }

//    fun hideProgressDialog() {
//        mProgressDialog.dismiss()
//    }

    fun doubleBackToExit() {
        // If back button pressed twice within 2 seconds the application closes
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        // If clicked once doubleBackToExitPressedOnce is set to true since one click and two clicks are dependent events
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            "Click again to exit",
            Toast.LENGTH_SHORT
        ).show()

        // If the back button is not pressed again after 2 seconds everything is reset
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    // Show errors using snack bar
    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }

    fun setActionBar(toolbar: androidx.appcompat.widget.Toolbar?, color: String) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = null
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            if(color == "w") {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_white_ios_24)
            } else if(color == "b") {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            }
            toolbar?.setNavigationOnClickListener {
                onBackPressed()
            }

        }
    }
    open fun getCurrentUserId() : String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun populateProductLists(list: ArrayList<Product>,rv_list: RecyclerView, tv: TextView,context: Context) {
        if(list.size > 0) {
            rv_list.visibility = View.VISIBLE
            tv.visibility = View.GONE
            rv_list.layoutManager = LinearLayoutManager(context)
            rv_list.setHasFixedSize(false)

            val adapter = ProductListAdapter(context,list)
            rv_list.adapter = adapter

            adapter.setOnClickListener(object: ProductListAdapter.OnClickListener{
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(context,ProductDetailActivity::class.java)
                    intent.putExtra(Constants.ID,product.id)
                    startActivity(intent)
                }
            })
            }
        else {
            rv_list.visibility = View.INVISIBLE
            tv.visibility = View.VISIBLE
        }
    }

    fun populateOrderLists(list: ArrayList<OrderedProduct>, rv_list: RecyclerView, tv: TextView, context: Context) {
        if(list.size > 0) {
            rv_list.visibility = View.VISIBLE
            tv.visibility = View.GONE
            rv_list.layoutManager = LinearLayoutManager(context)
            rv_list.setHasFixedSize(false)

            val adapter = OrdersListAdapter(context,list)
            rv_list.adapter = adapter

            when(context) {
                is CustomerMyOrdersActivity -> {
                    adapter.setOnClickListener(object: OrdersListAdapter.OnClickListener{
                        override fun onClick(position: Int, product: OrderedProduct) {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Your Order")
                            builder.setMessage("Please select what to do with your order. Select confirm pickup only after buying product")
                            builder.setPositiveButton("Confirm Pickup"){ dialogInterface,_ ->
                                dialogInterface.dismiss()
                                val intent = Intent(context,OrderedProductDetails::class.java)
                                intent.putExtra(Constants.ORDER_ID_WITHIN_APP,product.orderID)
                                Log.e("From item on click Unique ID",product.uniqueProductID.toString())
                                intent.putExtra(Constants.FARMER_ID_WITHIN_APP,product.uniqueProductID)
                                startActivity(intent)
                            }
                            builder.setNegativeButton("Cancel Order") { dialogInterface,_ ->
                                dialogInterface.dismiss()
                                FireBaseClass().deleteOrder(product.orderID)
                                startActivity(Intent(context,MainActivity::class.java))
                            }
                            builder.setNeutralButton("Cancel"){ dialogInterface,_ ->
                                dialogInterface.dismiss()
                            }
                            // Create the AlertDialog
                            val alertDialog: AlertDialog = builder.create()
                            // Set other dialog properties
                            alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
                            alertDialog.show()
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#77dd77"))
                            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#77dd77"))
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#77dd77"))
                        }
                    })
                }
            }
        }
        else {
            rv_list.visibility = View.INVISIBLE
            tv.visibility = View.VISIBLE
        }
    }
}