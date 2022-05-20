package com.villa.thesis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
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
                Intent(this@SignUpActivity, LogInActivity::class.java).also { toLogIn ->
                    toLogIn.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(toLogIn)

                    finish()
                }
            }

            btnSignUp.setOnClickListener {
                Intent(this@SignUpActivity, HomeActivity::class.java).also { toHome ->
                    toHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(toHome)

                    finish()
                }
            }

            showControl(false)

            btnSignUp.setOnClickListener {
                showControl(true)

                Intent(this@SignUpActivity, HomeActivity::class.java).also { toHome ->
                    toHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(toHome)

                    finish()
                }
            }
        }
    }

    private fun showControl(state: Boolean) {
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