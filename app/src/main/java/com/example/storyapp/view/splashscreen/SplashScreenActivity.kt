package com.example.storyapp.view.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.view.login.dataStore
import com.example.storyapp.view.utils.AuthViewModel
import com.example.storyapp.view.utils.PreferenceManager
import com.example.storyapp.view.welcome.WelcomeActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private var delayTime = 2000L
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.logoapp.setImageResource(R.drawable.logos)

        val prefen = PreferenceManager.getInstance(dataStore)
        val authViewModel =
            ViewModelProvider(this, SplashScreenModelFactory(prefen))[AuthViewModel::class.java]

        authViewModel.getUserLoginSession().observe(this) { isLoggedIn ->
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = if (isLoggedIn) {
                    Intent(this@SplashScreenActivity, MainActivity::class.java)
                } else {
                    Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }, delayTime)
        }
    }
}
