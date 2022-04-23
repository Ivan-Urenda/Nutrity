package com.example.nutrity.ui.forgot_pass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.nutrity.R
import com.example.nutrity.databinding.ActivityForgotPassBinding
import com.example.nutrity.ui.login.LoginActivity
import com.example.nutrity.uielements.Loading
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Forgot_pass_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPassBinding

    private lateinit var auth: FirebaseAuth

    private val loading = Loading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_forgot_pass)

        // Initializing FirebaseAuth
        auth = Firebase.auth

        with(binding) {
            emailField.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus) cleanErrors(emailField, emailLayout)
            }

            loginAccountLink.setOnClickListener {
                val intent = Intent(binding.root.context, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                finish()
            }

            submitBtn.setOnClickListener {
                val email = emailField.text.toString().trim()
                passwordReset(email)
            }
        }
    }

    private fun passwordReset(email: String) {
        if (!validateForm(email)) return

        loading.startDialog()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                loading.isDismiss()
                val snackbar = Snackbar.make(
                    binding.root,
                    "Password reset email sent. Verify your inbox.",
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(
                        "DISMISS", View.OnClickListener {
                            dismiss()
                        }
                    )
                }
                snackbar.show()
            }
            .addOnFailureListener { e ->
                Log.w("ForgotPass", "Something went wrong sending recovering the account. ", e)
                loading.isDismiss()
                val snackbar = Snackbar.make(
                    binding.root,
                    "Something went wrong. Try again later.",
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(
                        "DISMISS", View.OnClickListener {
                            dismiss()
                        }
                    )
                }
                snackbar.show()
            }
    }

    /*
    * Validate the form fields
    * Set helper text below the fields
    * */
    private fun validateForm(
        email: String,
    ): Boolean {
        var valid = true

        if (TextUtils.isEmpty(email)) {
            binding.emailField.error = "This field is required."
            binding.emailLayout.helperText = getString(R.string.signForm_helperText)
            binding.emailLayout.isHelperTextEnabled = true
            valid = false
        } else {
            binding.emailField.error = null
            binding.emailLayout.helperText = getString(R.string.blank)
            binding.emailLayout.isHelperTextEnabled = false
        }

        return valid
    }

    /*
    * Clean the helper texts in the form fields
    * */
    private fun cleanErrors(inputText: TextInputEditText, inputLayout: TextInputLayout) {
        inputText.error = null
        inputLayout.isHelperTextEnabled = false
        inputLayout.helperText = getString(R.string.blank)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}