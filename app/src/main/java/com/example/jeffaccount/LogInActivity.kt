package com.example.jeffaccount

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.databinding.LogInFragmentBinding
import com.example.jeffaccount.ui.MainActivity
import com.google.android.material.snackbar.Snackbar


class LogInActivity : AppCompatActivity() {

    private lateinit var loginBinding:LogInFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         loginBinding =
            DataBindingUtil.setContentView<LogInFragmentBinding>(this, R.layout.log_in_fragment)

        val loginViewModel = ViewModelProvider(this).get(LogInViewModel::class.java)
        val toolbar = loginBinding.logInToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        loginBinding.signInButton.setOnClickListener {

            val userEmail = loginBinding.logInEmailTextInput.text.toString()
            val password = loginBinding.logInPasswordTextInput.text.toString()
            if(isValidEmail(userEmail) && password.isNotEmpty()) {
                loginViewModel.loginUser(userEmail, password)
            }else if (!isValidEmail(userEmail)){
                loginBinding.logInEmailTextInputLayout.error = getString(R.string.enter_valid_email)
            }else if(password.isEmpty()){
                loginBinding.logInPasswordTextInputLayout.error = "Enter Password"
            }else{
                val snackbar = Snackbar.make(findViewById(R.id.login_coordinator_layout),"Enter Credential",Snackbar.LENGTH_SHORT)
                snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                snackbar.show()
            }
            
        }

        //Observe login message from viewModel
        loginViewModel.loginMessage.observe(this, Observer { message ->
            message?.let {
                if (message == "success") {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val snackbar = Snackbar.make(
                        findViewById<CoordinatorLayout>(R.id.login_coordinator_layout),
                        "Invalid Email or Password", Snackbar.LENGTH_SHORT
                    )
                    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        })
    }

    //Check if the email is valid
    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}
