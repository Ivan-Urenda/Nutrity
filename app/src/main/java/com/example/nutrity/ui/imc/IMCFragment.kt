package com.example.nutrity.ui.imc

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.nutrity.R
import com.example.nutrity.databinding.IMCFragmentBinding
import kotlin.math.pow

class IMCFragment : Fragment() {

    private var _binding: IMCFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(IMCViewModel::class.java)

        _binding = IMCFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val appContext = requireContext().applicationContext
        val spinner = binding.spinner
        val lista = listOf("Bajar de peso", "Subir de peso", "Mantener peso")
        val adaptador = ArrayAdapter(appContext, R.layout.spinner_items, lista)
        spinner.adapter = adaptador


        binding.calculateButton.setOnClickListener {

            if(binding.weight.text.toString().isEmpty() || binding.height.text.toString().isEmpty()){
                Toast.makeText(context, "Ingresa un valor valido", Toast.LENGTH_SHORT).show()
            }
            else if(binding.height.text.toString().toDouble() > 272){
                Toast.makeText(context, "Ingresa una altura valido", Toast.LENGTH_SHORT).show()
            }
            else{
                val weightValue = binding.weight.text.toString().toDouble()
                val heightValue = binding.height.text.toString().toDouble() / 100

                val imc = weightValue / heightValue.pow(2)

                if (imc < 16) {
                    binding.result.text = "IMC: " + imc + "\n\nEstado: Delgadez severa"
                } else if (imc < 18.5) {
                    binding.result.text = "IMC: " + imc + "\n\nEstado: Bajo peso"
                } else if (imc in 18.5..24.9) {
                    binding.result.text = "IMC: " + imc + "\n\nEstado: Normal"
                } else if (imc >= 25) {
                    binding.result.text = "IMC: " + imc + "\n\nEstado: Sobrepeso"
                } else {
                    binding.result.text = "IMC: " + imc + "\n\nEstado: Obesidad"
                }
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}