package com.smartherd.smartmart.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityCerealListBinding

class CerealListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCerealListBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCerealListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}