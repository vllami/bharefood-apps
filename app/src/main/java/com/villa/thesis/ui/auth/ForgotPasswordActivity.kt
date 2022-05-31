package com.villa.thesis.ui.auth

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns.EMAIL_ADDRESS
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.villa.thesis.R
import com.villa.thesis.databinding.ActivityForgotPasswordBinding

@Suppress("DEPRECATION")
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var activityForgotPasswordBinding: ActivityForgotPasswordBinding

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connectivityManager.also {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        it.getNetworkCapabilities(it.activeNetwork).apply {
                            if (this != null) {
                                return when {
                                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                                    else -> false
                                }
                            }
                        }
                    }
                }
            }

            return false
        }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            activityForgotPasswordBinding.apply {
                etForgotPasswordEmail.text.toString().trim().apply {
                    btnForgotPassword.isEnabled = isNotEmpty() && EMAIL_ADDRESS.matcher(this).matches()
                }
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        activityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)

        activityForgotPasswordBinding.apply {
            setContentView(root)

            when {
                !isNetworkAvailable -> {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.forgot_password_no_internet_snackbar), Snackbar.LENGTH_LONG).apply {
                        val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView?
                        val params = view.layoutParams as FrameLayout.LayoutParams

                        tv?.apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                textAlignment = View.TEXT_ALIGNMENT_CENTER
                            } else {
                                gravity = Gravity.CENTER_HORIZONTAL
                            }
                        }
                        params.gravity = Gravity.TOP
                        animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE

                        setTextColor(getColor(R.color.white))
                        setBackgroundTint(getColor(R.color.orange))

                        show()
                    }
                }
            }

            toolbarForgotPassword.setNavigationOnClickListener {
                onBackPressed()
            }

            etForgotPasswordEmail.apply {
                addTextChangedListener(textWatcher)

                setOnEditorActionListener { _, editorInfo, _ ->
                    when (editorInfo) {
                        EditorInfo.IME_ACTION_DONE -> clearFocus()
                    }

                    false
                }
            }

            showProgress(false)

            btnForgotPassword.setOnClickListener {
                val email = etForgotPasswordEmail.text.toString().trim()

                showProgress(true)

                firebaseAuth.apply {
                    sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        when {
                            task.isSuccessful -> {
                                showProgress(false)
                                showCustomToast(R.drawable.ic_mail, getString(R.string.forgot_password_success_toast, email))

                                finish()
                            }
                            else -> {
                                showProgress(false)
                                showCustomSnackbar(it, getString(R.string.forgot_password_user_not_found_snackbar), Snackbar.LENGTH_SHORT)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCustomToast(image: Int, message: String) {
        layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.toast_root) as? ViewGroup).also { layout ->
            layout.apply {
                findViewById<ImageView>(R.id.iv_toast).setImageResource(image)
                findViewById<TextView>(R.id.tv_toast_text).text = message

                Toast(applicationContext).apply {
                    duration = Toast.LENGTH_LONG
                    view = layout

                    setGravity(Gravity.CENTER, 0, 0)
                    show()
                }
            }
        }
    }

    private fun showCustomSnackbar(view: View, message: String, delay: Int) {
        Snackbar.make(view, message, delay).apply {
            anchorView = view
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE

            setTextColor(getColor(R.color.white))
            setBackgroundTint(getColor(R.color.dark_gray))

            show()
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