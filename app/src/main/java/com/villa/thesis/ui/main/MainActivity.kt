package com.villa.thesis.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.villa.thesis.R
import com.villa.thesis.databinding.ActivityHomeBinding
import com.villa.thesis.ui.start.SplashScreenActivity
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var calendar: Calendar
    private lateinit var activityHomeBinding: ActivityHomeBinding

    private var doubleBackToExit = false

    private val userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        calendar = Calendar.getInstance()
        activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)

        activityHomeBinding.apply {
            setContentView(root)

            // tvHomeGreetings.apply {
            //     calendar.also { calendar ->
            //         text = getString(
            //             R.string.home_user_greetings,
            //             when (calendar.get(Calendar.HOUR_OF_DAY)) {
            //                 in 0..10 -> getString(R.string.home_clock_0_10)
            //                 in 11..14 -> getString(R.string.home_clock_11_14)
            //                 in 15..18 -> getString(R.string.home_clock_15_18)
            //                 in 19..24 -> getString(R.string.home_clock_19_24)
            //                 else -> getString(R.string.home_welcome)
            //             }
            //         )
            //     }
            // }

            // tvHomeUserFirstName.apply {
            //     databaseReference.child(firebaseUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            //         override fun onDataChange(snapshot: DataSnapshot) {
            //             snapshot.getValue(User::class.java).also { user ->
            //                 text = user?.fullName?.first().toString()
            //             }
            //         }
            //
            //         override fun onCancelled(error: DatabaseError) {
            //             Toast.makeText(this@HomeActivity, "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show()
            //         }
            //     })
            // }

            btnLogout.setOnClickListener {
                firebaseAuth.signOut()
                Intent(this@HomeActivity, SplashScreenActivity::class.java).also { fromHomeToSplashScreen ->
                    startActivity(fromHomeToSplashScreen)

                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        when {
            doubleBackToExit -> {
                super.onBackPressed()
                return
            }
            else -> {
                this.doubleBackToExit = true
                Toast.makeText(this, "Tekan lagi untuk keluar", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExit = false
                }, DOUBLE_BACK_DELAY)
            }
        }
    }

    companion object {
        const val DOUBLE_BACK_DELAY = 2_500L
    }
}