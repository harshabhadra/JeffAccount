package com.example.jeffaccount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.jeffaccount.databinding.LogInFragmentBinding
import com.example.jeffaccount.ui.MainActivity

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginBinding = DataBindingUtil.setContentView<LogInFragmentBinding>(this,R.layout.log_in_fragment)

        val toolbar = loginBinding.logInToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        loginBinding.signInButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
