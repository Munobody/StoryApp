package com.example.storyapp.view.welcome


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.utils.PreferenceManager

class WelcomeActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PrefsWelcome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        preferenceManager = PrefsWelcome(this)
        supportActionBar?.hide()
        val btnStart = findViewById<Button>(R.id.btn_start)
        btnStart.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}