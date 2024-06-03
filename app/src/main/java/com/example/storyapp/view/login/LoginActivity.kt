package com.example.storyapp.view.login

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.LoginViewModelFactory
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.view.register.RegisterActivity
import com.example.storyapp.view.utils.AuthViewModel
import com.example.storyapp.view.utils.PreferenceManager
import com.example.storyapp.view.utils.SETTINGS_KEY
import com.example.storyapp.view.utils.getCurrentDateTime
import com.example.storyapp.view.utils.lightStatusBar
import com.google.android.material.snackbar.Snackbar

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_KEY)

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val delayTime: Long = 1000

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, LoginViewModelFactory(this))[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lightStatusBar(window, true)

        val prefen = PreferenceManager.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, LoginModelFactory(prefen))[AuthViewModel::class.java]

        val tvRegister = binding.tvRegister
        val tvFirstPart = getString(R.string.INFO_REGISTER_TEXT)
        val tvSecondPart = " " + getString(R.string.REGISTER_TEXT)
        val registerSpannable = generateSpannableString(tvFirstPart, tvSecondPart)
        tvRegister.text = registerSpannable
        tvRegister.movementMethod = LinkMovementMethod.getInstance()

        viewModel.isLoadingLogin.observe(this) {
            showLoading(it)
        }

        viewModel.loginStatus.observe(this) { loginStatus ->
            val isError = viewModel.isErrorLogin

            if (isError && !loginStatus.isNullOrEmpty()) {
                Snackbar.make(binding.root, loginStatus, Snackbar.LENGTH_SHORT).show()
            }
            if (isError && !loginStatus.isNullOrEmpty() && loginStatus == "User not found") {
                Snackbar.make(binding.root, getString(R.string.ERROR_PASSWORD_NOTSAME), Snackbar.LENGTH_SHORT).show()
            }
            else if (!isError && !loginStatus.isNullOrEmpty()) {
                val authLogin = viewModel.login.value
                val email = binding.etEmail.text.toString()
                authViewModel.saveUserLoginSession(true)
                authViewModel.saveUserLoginToken(authLogin?.loginResult!!.token)
                authViewModel.saveUserLoginName(authLogin.loginResult.name)
                authViewModel.saveUserLoginUid(authLogin.loginResult.userId)
                authViewModel.saveUserLoginEmail(email)
                authViewModel.saveUserLastLoginSession(getCurrentDateTime())
                Snackbar.make(binding.root, loginStatus, Snackbar.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }, delayTime)
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPass.text.toString()

            val emailError = if (email.isEmpty()) {
                getString(R.string.ERROR_EMAIL_EMPTY)
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                getString(R.string.ERROR_EMAIL_INVALID_FORMAT)
            } else null

            val passwordError = if (password.isEmpty()) {
                getString(R.string.ERROR_PASSWORD_EMPTY)
            } else if (password.length < 8) {
                getString(R.string.ERROR_PASSWORD_LENGTH)
            } else null

            binding.etEmail.error = emailError
            binding.etPass.error = passwordError

            val allErrors = listOf(emailError, passwordError)
            val anyErrors = allErrors.any { it != null }

            if (anyErrors) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.ERROR_EDITEXT_EMPTY),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                viewModel.login(email, password)
            }
        }
    }


    private fun generateSpannableString(firstPart: String, secondPart: String): Spannable {
        val spannable = SpannableString(firstPart + secondPart)
        val boldStyleSpan = StyleSpan(Typeface.BOLD)

        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val blueColor = ContextCompat.getColor(this, R.color.blue)
        spannable.setSpan(object : UnderlineSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }, 0, firstPart.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val startSecondPart = firstPart.length
        val endSecondPart = spannable.length
        spannable.setSpan(
            clickableSpan2,
            startSecondPart,
            endSecondPart,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(blueColor),
            startSecondPart,
            endSecondPart,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            boldStyleSpan,
            startSecondPart,
            endSecondPart,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
