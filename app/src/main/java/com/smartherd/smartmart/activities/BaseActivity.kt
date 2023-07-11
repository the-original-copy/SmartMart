package com.smartherd.smartmart.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.DialogProgressBinding

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

    open fun getCurrentUserId() : String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}