package com.example.nutrity.ui.configuracion

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.nutrity.R
import com.example.nutrity.ui.login.LoginActivity

class ConfiguracionFragment : Fragment() {

    private lateinit var btn: Button

    companion object {
        fun newInstance() = ConfiguracionFragment()
    }

    private lateinit var viewModel: ConfiguracionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.configuracion_fragment, container, false)

        btn = root.findViewById(R.id.Logout)
        btn.setOnClickListener {

            val intent:Intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }

        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfiguracionViewModel::class.java)
        // TODO: Use the ViewModel
    }



}