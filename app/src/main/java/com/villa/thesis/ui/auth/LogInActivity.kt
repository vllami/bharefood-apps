package com.villa.thesis.ui.auth

import android.content.Context
import android.content.Intent
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
import com.villa.thesis.databinding.ActivityLogInBinding
import com.villa.thesis.ui.main.MainActivity

@Suppress("DEPRECATION")
class LogInActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var activityLogInBinding: ActivityLogInBinding

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
            activityLogInBinding.apply {
                val emailInput = etLogInEmail.text.toString().trim()
                val passwordInput = etLogInPassword.text.toString().trim()

                btnLogIn.isEnabled = emailInput.isNotEmpty() && EMAIL_ADDRESS.matcher(emailInput).matches() && passwordInput.isNotEmpty()
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        activityLogInBinding = ActivityLogInBinding.inflate(layoutInflater)

        activityLogInBinding.apply {
            setContentView(root)

            when {
                !isNetworkAvailable -> {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.log_in_no_internet_snackbar), Snackbar.LENGTH_LONG).apply {
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

            toolbarLogIn.setNavigationOnClickListener {
                onBackPressed()
            }

            etLogInEmail.addTextChangedListener(textWatcher)

            etLogInPassword.apply {
                addTextChangedListener(textWatcher)

                setOnEditorActionListener { _, editorInfo, _ ->
                    when (editorInfo) {
                        EditorInfo.IME_ACTION_DONE -> clearFocus()
                    }

                    false
                }
            }

            tvLogInForgotPassword.setOnClickListener {
                Intent(this@LogInActivity, ForgotPasswordActivity::class.java).also { fromLogInToForgotPassword ->
                    fromLogInToForgotPassword.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(fromLogInToForgotPassword)
                }
            }

            tvLogInSignUpTextButton.setOnClickListener {
                Intent(this@LogInActivity, SignUpActivity::class.java).also { fromLogInToSignUp ->
                    fromLogInToSignUp.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(fromLogInToSignUp)

                    finish()
                }
            }

            btnLogIn.setOnClickListener {
                val email = etLogInEmail.text.toString().trim()
                val password = etLogInPassword.text.toString().trim()

                showProgress(true)

                firebaseAuth.apply {
                    signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        when {
                            task.isSuccessful -> {
                                if (currentUser?.isEmailVerified == true) {
                                    showProgress(false)
                                    showCustomToast(R.drawable.ic_success, getString(R.string.log_in_welcome_user_toast, email), Toast.LENGTH_SHORT)

                                    Intent(this@LogInActivity, MainActivity::class.java).also { fromLogInToHome ->
                                        startActivity(fromLogInToHome)

                                        finish()
                                    }
                                } else {
                                    currentUser?.reload()

                                    showProgress(false)
                                    showCustomToast(R.drawable.ic_mail, getString(R.string.log_in_not_verified_toast), Toast.LENGTH_LONG)
                                }
                            }
                            else -> {
                                showProgress(false)
                                showCustomToast(R.drawable.ic_error, getString(R.string.log_in_wrong_data_toast), Toast.LENGTH_SHORT)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCustomToast(image: Int, message: String, delay: Int) {
        layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.toast_root) as? ViewGroup).also { layout ->
            layout.apply {
                findViewById<ImageView>(R.id.iv_toast).setImageResource(image)
                findViewById<TextView>(R.id.tv_toast_text).text = message

                Toast(applicationContext).apply {
                    duration = delay
                    view = layout

                    setGravity(Gravity.CENTER, 0, 0)
                    show()
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