package com.example.nutrity.ui.forgot_pass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.nutrity.R
import com.example.nutrity.databinding.ActivityForgotPassBinding

class Forgot_pass_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_forgot_pass)
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