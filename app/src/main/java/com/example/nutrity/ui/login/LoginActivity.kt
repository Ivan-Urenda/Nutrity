package com.example.nutrity.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.nutrity.MainActivity
import com.example.nutrity.ProviderType
import com.example.nutrity.databinding.ActivityLoginBinding

import com.example.nutrity.R
import com.example.nutrity.ui.forgot_pass.Forgot_pass_Activity
import com.example.nutrity.ui.signup.SignupActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.loginBtn

        login.setOnClickListener {

            if(binding.username.text.isNotEmpty() && binding.password.text.isNotEmpty())
            {
                Firebase.auth
                    .signInWithEmailAndPassword(binding.username.text.toString(),
                        binding.password.text.toString()).addOnCompleteListener {

                            if (it.isSuccessful)
                            {
                                showHome(it.result.user?.email ?:"", ProviderType.BASIC)
                            }else
                            {
                                showAlert()
                            }
                    }
            }
        }

        val signup = binding.signup

        signup.setOnClickListener {
            val intent:Intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val pass = binding.recoverPass

        pass.setOnClickListener {
            val intent:Intent = Intent(this, Forgot_pass_Activity::class.java)
            startActivity(intent)
        }


    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("The email address or password is incorrect")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent:Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }
}

