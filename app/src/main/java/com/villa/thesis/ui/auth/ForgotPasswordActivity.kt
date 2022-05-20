package com.villa.thesis.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.villa.thesis.R
import com.villa.thesis.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var activityForgotPasswordBinding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)

        activityForgotPasswordBinding.apply {
            setContentView(root)

            toolbarForgotPassword.setNavigationOnClickListener {
                onBackPressed()
            }

            etForgotPasswordEmail.apply {
                setOnEditorActionListener { _, editorInfo, _ ->
                    when (editorInfo) {
                        EditorInfo.IME_ACTION_DONE -> clearFocus()
                    }

                    false
                }
            }

            showProgress(false)

            btnForgotPassword.setOnClickListener {
                showProgress(true)

                Snackbar.make(it, getString(R.string.forgot_password_snackbar), Snackbar.LENGTH_LONG).apply {
                    anchorView = it

                    setTextColor(getColor(R.color.white))
                    setBackgroundTint(getColor(R.color.dark_gray))
                    setActionTextColor(getColor(R.color.white))

                    animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE

                    show()
                }
            }
        }
    }

    private fun showProgress(state: Boolean) {
        activityForgotPasswordBinding.apply {
            when {
                state -> {
                    btnForgotPassword.visibility = View.INVISIBLE
                    cvForgotPasswordLoading.visibility = View.VISIBLE
                }
                else -> {
                    btnForgotPassword.visibility = View.VISIBLE
                    cvForgotPasswordLoading.visibility = View.INVISIBLE
                }
            }
        }
    }

}