package com.smartherd.smartmart

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.smartherd.smartmart.activities.BaseActivity
import com.smartherd.smartmart.activities.UserSignIn
import com.smartherd.smartmart.activities.UserSignUp
import com.smartherd.smartmart.databinding.ActivityIntroBinding
import com.smartherd.smartmart.databinding.DialogForgotBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding

class IntroActivity : BaseActivity() {
    var binding : ActivityIntroBinding? = null
    lateinit var dialogBinding: DialogForgotBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        auth = FirebaseAuth.getInstance()
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding?.tvAppNameIntro?.typeface = typeface

        binding?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this@IntroActivity, UserSignUp::class.java))
        }
        binding?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this@IntroActivity, UserSignIn::class.java))
        }
        binding?.tvForgotPassword?.setOnClickListener {
            val dialog = Dialog(this)
            dialogBinding = DialogForgotBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialogBinding.etEmail.requestFocus()
            val userEmail = dialogBinding.etEmail
            dialogBinding.btnReset.setOnClickListener{
                compareEmail(userEmail)
                dialog.dismiss()
            }
            dialogBinding.btnCancel.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun compareEmail(email : EditText) {
        val userEmail = email.text.toString().trim {it <= ' '}
        if (TextUtils.isEmpty(userEmail)) {
            showErrorSnackBar("Please enter email")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            showErrorSnackBar("Enter right email format")
            return
        }
        auth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@IntroActivity,
                        "Check your email spam for the reset link",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@IntroActivity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
    }
}