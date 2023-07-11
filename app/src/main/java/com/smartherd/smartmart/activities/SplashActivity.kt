package com.smartherd.smartmart.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.smartherd.smartmart.IntroActivity
import com.smartherd.smartmart.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        // START
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // END
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding.tvAppName.typeface = typeface

        // Adding the handler to after the a task after some delay.
        Handler().postDelayed({
            // Start the Intro Activity
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            finish() // Call this when your activity is done and should be closed and prevent the user from coming back to it when the back button is pressed
        }, 2000) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.
    }
}