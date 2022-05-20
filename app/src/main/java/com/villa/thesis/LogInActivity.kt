package com.villa.thesis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.villa.thesis.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var activityLogInBinding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityLogInBinding = ActivityLogInBinding.inflate(layoutInflater)

        activityLogInBinding.apply {
            setContentView(root)

            toolbarLogIn.setNavigationOnClickListener {
                onBackPressed()
            }

            etLogInPassword.apply {
                setOnEditorActionListener { _, editorInfo, _ ->
                    when (editorInfo) {
                        EditorInfo.IME_ACTION_DONE -> clearFocus()
                    }

                    false
                }
            }

            tvLogInSignUpTextButton.setOnClickListener {
                Intent(this@LogInActivity, SignUpActivity::class.java).also { fromLogInToSignUp ->
                    fromLogInToSignUp.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(fromLogInToSignUp)

                    finish()
                }
            }

            tvLogInForgotPassword.setOnClickListener {
                Intent(this@LogInActivity, ForgotPasswordActivity::class.java).also { fromLogInToForgotPassword ->
                    fromLogInToForgotPassword.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(fromLogInToForgotPassword)
                }
            }

            showProgress(false)

            btnLogIn.setOnClickListener {
                showProgress(true)

                Intent(this@LogInActivity, HomeActivity::class.java).also { fromLogInToHome ->
                    fromLogInToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(fromLogInToHome)

                    finish()
                }
            }
        }
    }

    private fun showProgress(state: Boolean) {
        activityLogInBinding.apply {
            when {
                state -> {
                    btnLogIn.visibility = View.INVISIBLE
                    cvLogInLoading.visibility = View.VISIBLE
                }
                else -> {
                    btnLogIn.visibility = View.VISIBLE
                    cvLogInLoading.visibility = View.INVISIBLE
                }
            }
        }
    }

}