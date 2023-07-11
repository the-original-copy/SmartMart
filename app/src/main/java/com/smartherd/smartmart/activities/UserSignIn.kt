package com.smartherd.smartmart.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityUserSignInBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.models.User

class UserSignIn : BaseActivity() {
    lateinit var binding: ActivityUserSignInBinding
    lateinit var auth: FirebaseAuth
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        binding.toolbarSignInActivity.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
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

    private fun signInRegisteredUser() {
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

       if(validateForm(email, password)) {
           showProgressDialog("Signing in...")
           auth.signInWithEmailAndPassword(email, password)
               .addOnCompleteListener{ task ->
                   hideProgressDialog()
                   if(task.isSuccessful) {
                       Toast.makeText(
                           this@UserSignIn,
                           "You have successfully signed in.",
                           Toast.LENGTH_LONG
                       ).show()
                       startActivity(Intent(this@UserSignIn, MainActivity::class.java))
                   } else {
                       Toast.makeText(
                           this@UserSignIn,
                           task.exception!!.message,
                           Toast.LENGTH_LONG
                       ).show()
                   }
               }
       }
    }

    private fun validateForm(email: String, password: String) : Boolean {
        return if(TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if(TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password")
            false
        } else {
            true
        }
    }

}