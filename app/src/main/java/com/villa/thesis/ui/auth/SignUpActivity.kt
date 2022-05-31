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
import com.google.firebase.database.FirebaseDatabase
import com.villa.thesis.ui.main.MainActivity
import com.villa.thesis.R
import com.villa.thesis.databinding.ActivitySignUpBinding
import com.villa.thesis.model.User

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var activitySignUpBinding: ActivitySignUpBinding

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
            activitySignUpBinding.apply {
                val fullNameInput = etSignUpFullName.text.toString().trim()
                val emailInput = etSignUpEmail.text.toString().trim()
                val passwordInput = etSignUpPassword.text.toString().trim()

                btnSignUp.isEnabled =
                    fullNameInput.isNotEmpty() &&
                    emailInput.isNotEmpty() && EMAIL_ADDRESS.matcher(emailInput).matches() &&
                    passwordInput.isNotEmpty() && passwordInput.length >= 8
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        activitySignUpBinding = ActivitySignUpBinding.inflate(layoutInflater)

        activitySignUpBinding.apply {
            setContentView(root)

            when {
                !isNetworkAvailable -> {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.sign_up_no_internet_snackbar), Snackbar.LENGTH_LONG).apply {
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

            toolbarSignUp.setNavigationOnClickListener {
                onBackPressed()
            }

            etSignUpFullName.addTextChangedListener(textWatcher)

            etSignUpEmail.addTextChangedListener(textWatcher)

            etSignUpPassword.apply {
                addTextChangedListener(textWatcher)

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

            btnSignUp.setOnClickListener {
                val fullName = etSignUpFullName.text.toString().trim()
                val email = etSignUpEmail.text.toString().trim()
                val password = etSignUpPassword.text.toString().trim()

                showProgress(true)

                firebaseAuth.apply {
                    createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        when {
                            task.isSuccessful -> {
                                firebaseDatabase.getReference(getString(R.string.sign_up_database_path))
                                    .child(currentUser!!.uid)
                                    .setValue(User(fullName, email))
                                    .addOnCompleteListener { firebaseDB ->
                                        if (firebaseDB.isSuccessful) {
                                            currentUser?.apply {
                                                sendEmailVerification()
                                                reload()
                                            }

                                            showProgress(false)
                                            showCustomToast(R.drawable.ic_success, getString(R.string.sign_up_success_toast, email), Toast.LENGTH_LONG)

                                            Intent(this@SignUpActivity, MainActivity::class.java).also { fromSignUpToHome ->
                                                startActivity(fromSignUpToHome)
                                            }
                                        } else {
                                            showProgress(false)
                                            showCustomToast(R.drawable.ic_error, getString(R.string.sign_up_something_wrong_toast), Toast.LENGTH_SHORT)
                                        }
                                    }
                            }
                            else -> {
                                showProgress(false)
                                showCustomToast(R.drawable.ic_error, getString(R.string.sign_up_email_already_exists_toast), Toast.LENGTH_SHORT)
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