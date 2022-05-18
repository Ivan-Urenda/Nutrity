package com.appNutrity.nutrity.ui.email_verification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.appNutrity.nutrity.databinding.EmailVerificationBinding
import com.appNutrity.nutrity.ui.login.LoginActivity
import com.appNutrity.nutrity.uielements.Loading
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailVerification : AppCompatActivity() {

    private lateinit var binding: EmailVerificationBinding

    private lateinit var auth: FirebaseAuth

    private var isEmailVerified: Boolean = true

    private val loading = Loading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = EmailVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        binding.nextButton.setOnClickListener {
            loading.startDialog()
            auth.signOut()
            loading.isDismiss()
            val intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            this.finish()
        }

        binding.resendEmail.setOnClickListener {
            loading.startDialog()

            val user = auth.currentUser

            user?.let { user ->
                user.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            loading.isDismiss()
                            user.reload()
                            // email has send, user is signed as default
                            val snackbar = Snackbar.make(
                                view,
                                "Verify your inbox.",
                                Snackbar.LENGTH_INDEFINITE
                            )
                            snackbar.setAction("DISMISS", View.OnClickListener {
                                snackbar.dismiss()
                            })
                            snackbar.show()
                        }
                    }
                    .addOnFailureListener { e ->
                        loading.isDismiss()
                        Log.w("EmailVerification", "Email verification error.", e)
                        val snackbar = Snackbar.make(
                            view,
                            "Something went wrong, try again please.",
                            Snackbar.LENGTH_INDEFINITE
                        )
                        snackbar.setAction("DISMISS", View.OnClickListener {
                            snackbar.dismiss()
                        })
                        snackbar.show()
                    }
            }
        }

        isEmailVerified = intent?.extras?.getString("emailVerified").toString().toBoolean()

        if (!isEmailVerified) {
            val snackbar = Snackbar.make(
                binding.root,
                "Please verify your account.",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction("DISMISS", View.OnClickListener {
                snackbar.dismiss()
            })
            snackbar.show()
        }
    }
}