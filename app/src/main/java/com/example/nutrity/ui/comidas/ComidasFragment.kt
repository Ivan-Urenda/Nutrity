package com.example.nutrity.ui.comidas

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.nutrity.R
import com.example.nutrity.databinding.ComidasFragmentBinding
import com.example.nutrity.databinding.FragmentRecetasBinding
import com.example.nutrity.ui.recetas.RecetasViewModel

class ComidasFragment : Fragment() {

    private var _binding: ComidasFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(ComidasViewModel::class.java)

        _binding = ComidasFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.ComidaBtn.setOnClickListener {

            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment_content_main)
            val navController = navHostFragment?.findNavController()
            navController?.navigate(R.id.platilloFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}