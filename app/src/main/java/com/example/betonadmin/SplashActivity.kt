package com.example.betonadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = Firebase.auth

        Thread {
            try {
                Thread.sleep(1500)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (auth.currentUser == null) {
                    startActivity(
                        Intent(
                            this,
                            SignInActivity::class.java
                        )
                    )
                } else {
                    startActivity(
                        Intent(
                            this,
                            HomeActivity::class.java
                        )
                    )
                }
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}