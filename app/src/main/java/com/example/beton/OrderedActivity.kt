package com.example.beton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.Exception

class OrderedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordered)
        Thread {
            try {
                Thread.sleep(1500)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {

                startActivity(
                    Intent(
                        this,
                        HomeActivity::class.java
                    )
                )

            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}