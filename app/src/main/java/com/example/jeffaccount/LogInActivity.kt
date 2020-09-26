package com.example.jeffaccount

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.dataBase.LogInCred
import com.example.jeffaccount.databinding.LogInFragmentBinding
import com.example.jeffaccount.model.CompanyDetails
import com.example.jeffaccount.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

private var loadingDialog: AlertDialog? = null

class LogInActivity : AppCompatActivity() {

    private lateinit var loginBinding: LogInFragmentBinding
    private lateinit var companyDetails: CompanyDetails
    private lateinit var logInCred: LogInCred
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding =
            DataBindingUtil.setContentView<LogInFragmentBinding>(this, R.layout.log_in_fragment)

        //Initializing ViewModel class
        val application = requireNotNull(this).application as JeffApplication
        val loginViewModelFactory = LoginViewModelFactory(application)
        val loginViewModel =
            ViewModelProvider(this, loginViewModelFactory).get(LogInViewModel::class.java)
        val toolbar = loginBinding.logInToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        //Create Loading Dialog
        loadingDialog = createLoadingDialog()

        //Set On Click listener to the sign in button
        loginBinding.signInButton.setOnClickListener {

            var userEmail = loginBinding.logInEmailTextInput.text.toString()
            var password = loginBinding.logInPasswordTextInput.text.toString()
            if (isValidEmail(userEmail) && password.isNotEmpty()) {
                loadingDialog?.show()
                //Log in user
                logInCred = LogInCred(userName = userEmail, password = password)
                loginViewModel.insertLoginCred(logInCred)

            } else if (!isValidEmail(userEmail)) {
                loginBinding.logInEmailTextInputLayout.error = getString(R.string.enter_valid_email)
            } else if (password.isEmpty()) {
                loginBinding.logInPasswordTextInputLayout.error = "Enter Password"
            } else {
                val snackbar = Snackbar.make(
                    findViewById(R.id.login_coordinator_layout),
                    "Enter Credential",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                snackbar.show()
            }
        }

        loginViewModel.userList.observe(this, Observer {
            it?.let {
                Timber.e("Size: ${it.size}")
                if(it.isNotEmpty()) {
                    loadingDialog?.show()
                    logInCred = it[0]
                    loginBinding.logInEmailTextInput.setText(it[0].userName)
                    loginBinding.logInPasswordTextInput.setText(it[0].password)
                    loginViewModel.loginUser(it[0].userName!!, it[0].password!!)
                }
            }?:let {
                Timber.e("list is empty")
            }
        })

        //Observe login message from viewModel
        loginViewModel.loginMessage.observe(this, Observer { message ->
            message?.let {
                loadingDialog?.dismiss()
                if (it.message == "success") {
                    companyDetails = it
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("login",logInCred)
                    intent.putExtra("company",companyDetails)
                    loginViewModel.loginCompleted()
                    startActivity(intent)
                    finish()
                } else {
                    loginViewModel.deleteLogInCred(logInCred)
                    val snackbar = Snackbar.make(
                        findViewById<CoordinatorLayout>(R.id.login_coordinator_layout),
                        "Invalid Email or Password", Snackbar.LENGTH_SHORT
                    )
                    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }?:let {
                loadingDialog?.dismiss()
            }
        })

        //Add text watcher to email
        loginBinding.logInEmailTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loginBinding.logInEmailTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginBinding.logInEmailTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text watcher to password
        loginBinding.logInPasswordTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loginBinding.logInPasswordTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginBinding.logInPasswordTextInputLayout.isErrorEnabled = false
            }
        })
    }

    //Check if the email is valid
    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    //Create a loading Dialog
    private fun createLoadingDialog(): androidx.appcompat.app.AlertDialog? {
        val layout = LayoutInflater.from(this).inflate(R.layout.loading_layout, null)
        val builder = this.let { androidx.appcompat.app.AlertDialog.Builder(it) }
        builder.setCancelable(false)
        builder.setView(layout)
        return builder.create()
    }
}
