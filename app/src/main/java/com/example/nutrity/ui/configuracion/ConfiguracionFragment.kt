package com.example.nutrity.ui.configuracion

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.nutrity.MainActivity
import com.example.nutrity.R
import com.example.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.example.nutrity.databinding.ConfiguracionFragmentBinding
import com.example.nutrity.ui.login.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ConfiguracionFragment : Fragment() {

    private var _binding: ConfiguracionFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ConfiguracionFragment()
    }

    private lateinit var viewModel: ConfiguracionViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ConfiguracionFragmentBinding.inflate(inflater, container, false)

        val root = binding.root

        binding.tvUsername.text = prefs.getUsername()
        binding.email.text = Firebase.auth.currentUser?.email
        binding.firstName.text = prefs.getFirstName()
        binding.lastName.text = prefs.getLastName()
        binding.tvUsername2.text = prefs.getUsername()
        binding.caloriesGoal.text = prefs.getCalories().toString()+" kcal"

        binding.Logout.setOnClickListener {

            Firebase.auth.signOut()
            prefs.wipe()
            val intent: Intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfiguracionViewModel::class.java)
        // TODO: Use the ViewModel
    }


}