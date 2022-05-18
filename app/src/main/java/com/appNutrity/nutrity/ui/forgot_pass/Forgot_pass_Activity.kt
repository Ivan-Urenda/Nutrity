package com.appNutrity.nutrity.ui.forgot_pass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.appNutrity.nutrity.R
import com.appNutrity.nutrity.databinding.ActivityForgotPassBinding
import com.appNutrity.nutrity.ui.login.LoginActivity
import com.appNutrity.nutrity.uielements.Loading
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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing FirebaseAuth
        auth = Firebase.auth

        with(binding) {
            emailPasswordReset.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus) cleanErrors(emailPasswordReset, emailPasswordResetLayout)
            }

            loginAccountAfterLink.setOnClickListener {
                val intent = Intent(binding.root.context, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                finish()
            }

            passwordResetBtn.setOnClickListener {
                try {
                    val email = emailPasswordReset.text.toString().trim()
                    passwordReset(email)
                    Log.d("PassReset", "Password resent event applied.")
                } catch (e: Exception) {
                    Log.e("PassReset", "Somethign went wrong.", e)
                }
            }
        }
    }

    private fun passwordReset(email: String) {
        if (!validateForm(email)) return

        loading.startDialog()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if(it.isSuccessful) {
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
            binding.emailPasswordReset.error = "This field is required."
            binding.emailPasswordResetLayout.helperText = getString(R.string.signForm_helperText)
            binding.emailPasswordResetLayout.isHelperTextEnabled = true
            valid = false
        } else {
            binding.emailPasswordReset.error = null
            binding.emailPasswordResetLayout.helperText = getString(R.string.blank)
            binding.emailPasswordResetLayout.isHelperTextEnabled = false
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