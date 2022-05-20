package com.appNutrity.nutrity.ui.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.appNutrity.nutrity.MainActivity
import com.appNutrity.nutrity.R
import com.appNutrity.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.appNutrity.nutrity.databinding.ActivityLoginBinding
import com.appNutrity.nutrity.ui.email_verification.EmailVerification
import com.appNutrity.nutrity.ui.forgot_pass.Forgot_pass_Activity
import com.appNutrity.nutrity.ui.signup.SignupActivity
import com.appNutrity.nutrity.ui.user_profile.UserProfileConfig
import com.appNutrity.nutrity.uielements.Loading
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private var permission = false

    // Loading Alert Modal
    private val loading = Loading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

        Toast.makeText(applicationContext, prefs.getUri(), Toast.LENGTH_SHORT).show()
        requestPermissions()
    }

    private fun checkUserValues() {
        if (prefs.getLogged()) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permission = true
                checkUserValues()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            checkUserValues()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if(isGranted) {
            permission = true
        }
    }

    private fun alertDialog(){
        try {
            AlertDialog.Builder(this).apply {
                setTitle("Alert")
                setMessage("Nutrity needs permission to access your media files to function properly. Activate them to continue")
                setPositiveButton("Acept") { _: DialogInterface, _: Int ->}
            }.show()
        }catch (e: Exception){

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

            var state = false

            if (binding.checkBox.isChecked) {
                state = true
            }

            withContext(Dispatchers.IO){

                val request = Volley.newRequestQueue(applicationContext)
                var url = "https://ivanurenda.000webhostapp.com/UserData.php?email=${email}"
                url=url.replace(" ", "%20")
                val stringRequest = StringRequest(Request.Method.GET, url, { response ->

                    val jsonArray = JSONArray(response)
                    val jsonObject = JSONObject(jsonArray.getString(0))
                    setValuesPrefs(jsonObject, email, state)
                }, { error ->

                })
                request.add(stringRequest)

            }
        }
    }

    private fun setValuesPrefs(jsonObject: JSONObject, email: String, state: Boolean)
    {
        if (jsonObject.get("profileEdited").toString().toInt() == 0){
            loading.isDismiss()
            if (permission){
                prefs.wipe()
                redirectToUserProfile(email, state)
            }else {
                alertDialog()
            }
        }
        else{
            prefs.saveLogged(state)
            prefs.saveCalories(jsonObject.get("calories").toString().toInt())
            prefs.saveProgress(jsonObject.get("progress").toString().toInt())
            prefs.saveProteins(jsonObject.get("proteins").toString().toInt())
            prefs.saveCarbs(jsonObject.get("carbs").toString().toInt())
            prefs.saveFats(jsonObject.get("fats").toString().toInt())
            prefs.saveUsername(jsonObject.get("username").toString())
            prefs.saveFirstName(jsonObject.get("firstName").toString())
            prefs.saveLastName(jsonObject.get("lastName").toString())
            prefs.saveWeight(jsonObject.get("weight").toString().toInt())
            prefs.saveHeight(jsonObject.get("height").toString().toInt())
            prefs.saveAge(jsonObject.get("age").toString().toInt())
            prefs.saveObjective(jsonObject.get("objective").toString())
            prefs.saveUri(jsonObject.get("uriImage").toString())
            prefs.saveLogged(state)
            loading.isDismiss()
            if (permission){
                redirectToHome(email)
            }else {
                alertDialog()
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

