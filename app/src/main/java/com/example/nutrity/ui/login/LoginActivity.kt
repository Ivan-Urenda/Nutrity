package com.example.nutrity.ui.login

import android.app.Activity
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
import com.example.nutrity.databinding.ActivityLoginBinding

import com.example.nutrity.R
import com.example.nutrity.ui.forgot_pass.Forgot_pass_Activity
import com.example.nutrity.ui.signup.SignupActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login

        login.setOnClickListener {

            if(binding.username.text.toString() == "nutrity" && binding.password.text.toString() == "admin")
            {
                val intent:Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
            {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
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
}

