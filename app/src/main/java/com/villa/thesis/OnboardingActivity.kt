package com.villa.thesis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.villa.thesis.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var activityOnboardingBinding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityOnboardingBinding = ActivityOnboardingBinding.inflate(layoutInflater)

        activityOnboardingBinding.apply {
            setContentView(root)

            btnOnboardingLogIn.setOnClickListener {
                Intent(this@OnboardingActivity, LogInActivity::class.java).also { fromOnboardingToLogIn ->
                    startActivity(fromOnboardingToLogIn)
                }
            }

            btnOnboardingSignUp.setOnClickListener {
                Intent(this@OnboardingActivity, SignUpActivity::class.java).also { fromOnboardingToSignUp ->
                    startActivity(fromOnboardingToSignUp)
                }
            }
        }
    }

}