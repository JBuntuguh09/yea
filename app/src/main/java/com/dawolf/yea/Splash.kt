package com.dawolf.yea

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dawolf.yea.databinding.ActivitySplashBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navTo()
    }

    private fun navTo() {
//        GlobalScope.launch {
//            delay(3000)
//
//        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this@Splash, MainActivity::class.java))
            finish()
        }
    }
}