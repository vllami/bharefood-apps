package com.villa.thesis.ui.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.villa.thesis.databinding.ActivitySplashScreenBinding
import com.villa.thesis.utils.Constants.SPLASH_DELAY

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var activitySplashScreenBinding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activitySplashScreenBinding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(activitySplashScreenBinding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, OnboardingActivity::class.java).also { toOnboarding ->
                startActivity(toOnboarding)
                finish()
            }
        }, SPLASH_DELAY.toLong())
    }

}