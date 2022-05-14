package com.example.nutrity.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nutrity.ui.email_verification.EmailVerification
import com.example.nutrity.MainActivity
import com.example.nutrity.R
import com.example.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.example.nutrity.databinding.ActivityLoginBinding
import com.example.nutrity.ui.forgot_pass.Forgot_pass_Activity
import com.example.nutrity.ui.signup.SignupActivity
import com.example.nutrity.ui.user_profile.UserProfileConfig
import com.example.nutrity.uielements.Loading
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    // Loading Alert Modal
    private val loading = Loading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        with(binding) {
            // Redirects to the Sign Up Activity
            createAccountLink.setOnClickListener { redirectToCreateAccount() }

            // Text input watcher that clean the helper texts
            emailField.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    cleanErrors(emailField, emailFieldLayout)
                }
            }

            // Text input watcher that clean the helper texts
            passwordField.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    cleanErrors(passwordField, passwordFieldLayout)
                }
            }

            forgotPassBtn.setOnClickListener { redirectToForgetPassword() }

            // User sign in method
            btnLogin.setOnClickListener {
                val emailValue = emailField.text.toString().trim()
                val passwordValue = passwordField.text.toString().trim()
                signIn(emailValue, passwordValue)
            }
        }

        checkUserValues()
    }

    private fun checkUserValues() {
        if (prefs.getLogged()) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }

    /*
    * Redirects to the sign up activity
    * */
    private fun redirectToCreateAccount() {
        val intent = Intent(this, SignupActivity::class.java).apply {
            putExtra("createAccount", binding.createAccountLink.text.toString())
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        this.finish()
    }

    /*
    * Redirects to the Main Activity
    * */
    private fun redirectToHome(email: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        this.finish()
    }

    /*
    * Redirects to the Forget Password Activity
    * */
    private fun redirectToForgetPassword() {
        val intent = Intent(this, Forgot_pass_Activity::class.java)
        startActivity(intent)
    }

    /*
    * Redirects to the User Profile Configuration Activity
    * */
    private fun redirectToUserProfile(email: String, state: Boolean) {
        val intent = Intent(this, UserProfileConfig::class.java).apply {
            putExtra("email", email)
            putExtra("state", state)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        this.finish()
    }

    private fun userHasAProfile(email: String) {
        GlobalScope.launch(Dispatchers.Main) {

            val userHasEditedProfile = prefs.getConfig()

            var calories: Int? = null
            var progress: Int? = null
            var proteins: Int? = null
            var carbs: Int? = null
            var fats: Int? = null
            var username: String? = null
            var firstName: String? = null
            var lastName: String? = null

            var state = false

            if (binding.checkBox.isChecked) {
                state = true
            }

            withContext(Dispatchers.IO){

                if (userHasEditedProfile){
                    prefs.saveLogged(state)
                    val request = Volley.newRequestQueue(applicationContext)
                    var url = "https://ivanurenda.000webhostapp.com/Calories.php?email=${email}"
                    url=url.replace(" ", "%20")
                    val stringRequest = StringRequest(Request.Method.GET, url, { response ->

                        val jsonArray = JSONArray(response)
                        val jsonObject = JSONObject(jsonArray.getString(0))
                        progress = jsonObject.get("progress").toString().toInt()
                        calories = jsonObject.get("calories").toString().toInt()
                        proteins = jsonObject.get("proteins").toString().toInt()
                        carbs = jsonObject.get("carbs").toString().toInt()
                        fats = jsonObject.get("fats").toString().toInt()
                        username = jsonObject.get("username").toString()
                        firstName = jsonObject.get("firstName").toString()
                        lastName = jsonObject.get("lastName").toString()
                    }, { error ->

                    })
                    request.add(stringRequest)
                }

            }
            delay(1000)
            if (!userHasEditedProfile) {
                loading.isDismiss()
                redirectToUserProfile(email, state)
            }else{
                loading.isDismiss()
                prefs.saveCalories(calories!!)
                prefs.saveProgress(progress!!)
                prefs.saveProteins(proteins!!)
                prefs.saveCarbs(carbs!!)
                prefs.saveFats(fats!!)
                prefs.saveUsername(username!!)
                prefs.saveFirstName(firstName!!)
                prefs.saveLastName(lastName!!)
                redirectToHome(email)
            }
        }
    }

    /*
    * Sign user with Firebase
    * */
    private fun signIn(
        email: String,
        password: String,
    ) {
        Log.d(TAG, "Sign In: $email")

        if (!validateForm(email, password)) return

        loading.startDialog()

        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                when (auth.currentUser?.isEmailVerified) {
                    true -> {
                        userHasAProfile(email)
                    }
                    else -> {
                        // User has not been verified
                        loading.isDismiss()
                        val intent = Intent(
                            this,
                            EmailVerification::class.java
                        ).apply {
                            putExtra("emailVerified", false)
                        }
                        startActivity(intent)
                    }
                }
            } else {
                loading.isDismiss()
                val snackbar = Snackbar.make(
                    binding.root,
                    "Authentication failed. Try again.",
                    Snackbar.LENGTH_INDEFINITE
                )

                with(snackbar) {
                    setAction("DISMISS", View.OnClickListener {
                        snackbar.dismiss()
                    })
                    show()
                }
            }
        }
    }

    /*
    * Validate the form fields
    * Set helper text below the fields
    * */
    private fun validateForm(
        email: String,
        password: String,
    ): Boolean {
        var valid = true

        if (TextUtils.isEmpty(email)) {
            binding.emailField.error = "This field is required."
            binding.emailFieldLayout.helperText = getString(R.string.signForm_helperText)
            binding.emailFieldLayout.isHelperTextEnabled = true
            valid = false
        } else {
            binding.emailField.error = null
            binding.emailFieldLayout.helperText = getString(R.string.blank)
            binding.emailFieldLayout.isHelperTextEnabled = false
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordFieldLayout.helperText = getString(R.string.signForm_helperText)
            binding.passwordFieldLayout.isHelperTextEnabled = true
            valid = false
        } else {
            binding.passwordField.error = null
            binding.passwordFieldLayout.helperText = getString(R.string.blank)
            binding.passwordFieldLayout.isHelperTextEnabled = false
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

    companion object {
        private const val TAG = "EmailPassword"
    }

}

