package com.example.nutrity.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nutrity.MainActivity
import com.example.nutrity.ProviderType
import com.example.nutrity.R
import com.example.nutrity.databinding.ActivitySignupBinding
import com.example.nutrity.ui.email_verification.EmailVerification
import com.example.nutrity.ui.login.LoginActivity
import com.example.nutrity.uielements.Loading
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    // Loading Dialog Alert
    private val loading = Loading(this)

    private lateinit var fireDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing Firebase Auth Instance
        auth = Firebase.auth

        // Initializing Firebase Firestore instance
        fireDb = Firebase.firestore

        with(binding) {
            btnCreateAccount.setOnClickListener {
                val emailValue = newEmailField.text.toString().trim()
                val passwordValue = newPasswordField.text.toString().trim()
                val confirmPassword = confirmPasswordField.text.toString().trim()
                createAccount(emailValue, passwordValue, confirmPassword)
            }

            loginAccountLink.setOnClickListener { redirectToLoginAccount() }

            newEmailField.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    cleanErrors(newEmailField, newEmailLayout)
                }
            }

            newPasswordField.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    cleanErrors(newPasswordField, newPasswordLayout)
                }
            }

            confirmPasswordField.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    cleanErrors(confirmPasswordField, confirmPasswordLayout)
                }
            }
        }
    }

    private fun redirectToLoginAccount() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("loginAccount", binding.loginAccountLink.text.toString())
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        this.finish()
    }

    private fun redirectToHome(
        email: String,
        providerType: ProviderType,
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("new email", email)
            putExtra("new providerType", providerType.name)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        this.finish()
    }

    private fun createAccount(
        email: String,
        password: String,
        confirmPassword: String,
    ) {
        if (!validateForm(email, password, confirmPassword)) return

        loading.startDialog()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadUser(email)
                    loading.isDismiss()
                    sendVerificationEmail(email, ProviderType.BASIC)
                } else {
                    loading.isDismiss()
                    Snackbar.make(
                        binding.root,
                        "Registration failed. Try Again.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun uploadUser(email: String) {
        val request = Volley.newRequestQueue(this)

        var url = "https://ivanurenda.000webhostapp.com/Registro.php?email=${email}"
        url=url.replace(" ", "%20")
        var stringRequest = StringRequest(Request.Method.GET, url, { response ->

            Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show()
        }, { error ->

        })
        request.add(stringRequest)
    }

    private fun sendVerificationEmail(email: String, providerType: ProviderType) {
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            if (!emailVerified) {
                user.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // email has send, user is signed as default
                            auth.signOut()
                            val intent = Intent(this, EmailVerification::class.java).apply {
                                putExtra("createAccountEmail", false)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                            this.finish()
                        }
                    }
            } else {
                redirectToHome(email, providerType)
            }
        }
    }

    private fun validateForm(
        email: String,
        password: String,
        confirmPassword: String,
    ): Boolean {
        var valid = true

        with(binding) {
            if (TextUtils.isEmpty(email)) {
                newEmailField.error = getString(R.string.signForm_helperText)
                newEmailLayout.helperText = getString(R.string.signForm_helperText)
                newEmailLayout.isHelperTextEnabled = true
                valid = false
            } else {
                newEmailField.error = null
                newEmailLayout.helperText = getString(R.string.blank)
                newEmailLayout.isHelperTextEnabled = false
            }

            if (TextUtils.isEmpty(confirmPassword) && TextUtils.isEmpty(password)) {
                newPasswordLayout.helperText = getString(R.string.signForm_helperText)
                confirmPasswordLayout.helperText = getString(R.string.signForm_helperText)
                newPasswordLayout.isHelperTextEnabled = true
                confirmPasswordLayout.isHelperTextEnabled = true
                valid = false
            } else if (TextUtils.isEmpty(confirmPassword)) {
                confirmPasswordLayout.helperText = getString(R.string.signForm_helperText)
                confirmPasswordLayout.isHelperTextEnabled = true
                valid = false
            } else if (TextUtils.isEmpty(password)) {
                newPasswordLayout.helperText = getString(R.string.signForm_helperText)
                newPasswordLayout.isHelperTextEnabled = true
                valid = false
            } else if (password != confirmPassword) {
                Snackbar.make(
                    binding.root,
                    R.string.password_dntMatch,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                valid = false
            } else {
                newPasswordField.error = null
                confirmPasswordField.error = null
                newPasswordLayout.isHelperTextEnabled = false
                confirmPasswordLayout.isHelperTextEnabled = false
                newPasswordLayout.helperText = getString(R.string.blank)
                confirmPasswordLayout.helperText = getString(R.string.blank)
            }
        }

        return valid
    }

    private fun cleanErrors(
        inputText: TextInputEditText,
        inputLayout: TextInputLayout,
    ) {
        inputText.error = null
        inputLayout.isHelperTextEnabled = false
        inputLayout.helperText = getString(R.string.blank)
    }

    companion object {
        private const val TAG = "CreateAccountStatus"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}