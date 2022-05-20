package com.villa.thesis.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.villa.thesis.HomeActivity
import com.villa.thesis.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var activitySignUpBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activitySignUpBinding = ActivitySignUpBinding.inflate(layoutInflater)

        activitySignUpBinding.apply {
            setContentView(root)

            toolbarSignUp.setNavigationOnClickListener {
                onBackPressed()
            }

            etSignUpPassword.apply {
                setOnEditorActionListener { _, editorInfo, _ ->
                    when (editorInfo) {
                        EditorInfo.IME_ACTION_DONE -> clearFocus()
                    }

                    false
                }
            }

            tvSignUpLogInTextButton.setOnClickListener {
                Intent(this@SignUpActivity, LogInActivity::class.java).also { fromSignUpToLogIn ->
                    fromSignUpToLogIn.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(fromSignUpToLogIn)

                    finish()
                }
            }

            showProgress(false)

            btnSignUp.setOnClickListener {
                showProgress(true)

                Intent(this@SignUpActivity, HomeActivity::class.java).also { fromSignUpToHome ->
                    fromSignUpToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(fromSignUpToHome)

                    finish()
                }
            }
        }
    }

    private fun showProgress(state: Boolean) {
        activitySignUpBinding.apply {
            when {
                state -> {
                    btnSignUp.visibility = View.INVISIBLE
                    cvSignUpLoading.visibility = View.VISIBLE
                }
                else -> {
                    btnSignUp.visibility = View.VISIBLE
                    cvSignUpLoading.visibility = View.INVISIBLE
                }
            }
        }
    }

}