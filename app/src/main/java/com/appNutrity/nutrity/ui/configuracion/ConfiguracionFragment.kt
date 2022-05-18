package com.appNutrity.nutrity.ui.configuracion

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appNutrity.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.appNutrity.nutrity.databinding.ConfiguracionFragmentBinding
import com.appNutrity.nutrity.ui.login.LoginActivity
import com.appNutrity.nutrity.ui.user_profile.UserProfileConfig
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ConfiguracionFragment : Fragment() {

    private var _binding: ConfiguracionFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ConfiguracionFragment()
    }

    private lateinit var viewModel: ConfiguracionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ConfiguracionFragmentBinding.inflate(inflater, container, false)

        val root = binding.root

        if (prefs.getCalories() == 0) {
            alert()
        } else {
            setValues()
        }

        binding.EditProfile.setOnClickListener {
            val intent = Intent(context, UserProfileConfig::class.java).apply {
                putExtra("email", Firebase.auth.currentUser?.email)
                putExtra("state", prefs.getLogged())
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        binding.Logout.setOnClickListener {

            Firebase.auth.signOut()
            prefs.wipe()
            val intent: Intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return root

    }

    @SuppressLint("SetTextI18n")
    fun setValues(){
        binding.tvUsername.text = prefs.getUsername()
        binding.email.text = Firebase.auth.currentUser?.email
        binding.caloriesGoal.text = prefs.getCalories().toString()+" kcal"
        binding.Name.text = prefs.getFirstName()+" "+ prefs.getLastName()
        binding.weight.text = prefs.getWeight().toString()+" kg"
        binding.height.text = prefs.getHeight().toString()+" cm"
        binding.age.text = prefs.getAge().toString()+" years"
        binding.objetive.text = prefs.getObjective()

    }

    private fun alert() {

        AlertDialog.Builder(context).apply {
            setTitle("Nutrity")
            setMessage("You should first calculate your target calories before viewing your profile.")
            setPositiveButton("Acept") { _: DialogInterface, _: Int ->
            }
        }.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfiguracionViewModel::class.java)
        // TODO: Use the ViewModel
    }


}