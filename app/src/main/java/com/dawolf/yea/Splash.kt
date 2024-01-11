package com.dawolf.yea

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        navTo()
    }

    private fun navTo() {
        GlobalScope.launch {
            delay(3000)
            startActivity(Intent(this@Splash, MainActivity::class.java))
            finish()
        }
    }
}