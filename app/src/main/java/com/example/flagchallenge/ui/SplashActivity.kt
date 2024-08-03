package com.example.flagchallenge.ui


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.flagchallenge.R

class SplashActivity : AppCompatActivity() {

    private val splashScreenDuration: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // Start the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, splashScreenDuration)
    }
}
