package com.example.nutrity.ui.imc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nutrity.MainActivity
import com.example.nutrity.R
import com.example.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.example.nutrity.databinding.IMCFragmentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class IMCFragment : Fragment() {

    private var _binding: IMCFragmentBinding? = null
    private val db = Firebase.firestore
    private lateinit var objetivo: Spinner
    private lateinit var genero: Spinner
    private lateinit var fActividad: Spinner

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
        objetivo = binding.objetivo
        val objectives = listOf("Lose weight", "Gain weight", "Maintain weight")
        var adaptador = ArrayAdapter(appContext, R.layout.spinner_items, objectives)
        objetivo.adapter = adaptador

        genero = binding.genero
        val genres = listOf("Men", "Women")
        adaptador = ArrayAdapter(appContext, R.layout.spinner_items, genres)
        genero.adapter = adaptador

        fActividad = binding.fActividad
        val fActivities = listOf("Sedentary", "Little activity", "Moderate activity", "Intense activity")
        adaptador = ArrayAdapter(appContext, R.layout.spinner_items, fActivities)
        fActividad.adapter = adaptador

        binding.calculateButton.setOnClickListener {

            var cal: Int
            if(binding.genero.selectedItem.toString() == "Men"){
                if(binding.weight.text.toString().isEmpty() || binding.height.text.toString().isEmpty() || binding.age.toString().isEmpty()){
                    Toast.makeText(context, "Please enter a valid value", Toast.LENGTH_SHORT).show()
                }
                else if(binding.height.text.toString().toDouble() > 272){
                    Toast.makeText(context, "Please enter a valid height", Toast.LENGTH_SHORT).show()
                }else if(binding.age.text.toString().toDouble() > 100){
                    Toast.makeText(context, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                }
                else{
                    if(binding.fActividad.selectedItem.toString() == "Sedentary")
                    {
                        cal = ((66 + (13.7 * binding.weight.text.toString().toDouble()) +
                                ((5 * binding.height.text.toString().toDouble()) - (6.8 * binding.age.text.toString().toDouble()))) * 1.2).roundToInt()

                        if(binding.objetivo.selectedItem.toString() == "Lose weight")
                        {
                            binding.result.text = "You must consume less than "+cal+" calories to lose weight"

                        }else if(binding.objetivo.selectedItem.toString() == "Gain weight"){

                            binding.result.text = "You must consume more than "+cal+" calories to gain weight"

                        }
                        else
                        {
                            binding.result.text = "You must consume "+cal+" calories to maintain your weight"
                        }

                    }else if(binding.fActividad.selectedItem.toString() == "Little activity"){

                        cal = ((66 + (13.7 * binding.weight.text.toString().toDouble()) +
                                ((5 * binding.height.text.toString().toDouble()) - (6.8 * binding.age.text.toString().toDouble()))) * 1.375).roundToInt()

                        if(binding.objetivo.selectedItem.toString() == "Lose weight")
                        {
                            binding.result.text = "You must consume less than "+cal+" calories to lose weight"

                        }else if(binding.objetivo.selectedItem.toString() == "Gain weight"){

                            binding.result.text = "You must consume more than "+cal+" calories to gain weight"

                        }
                        else
                        {
                            binding.result.text = "You must consume "+cal+" calories to maintain your weight"
                        }

                    }else if(binding.fActividad.selectedItem.toString() == "Moderate activity"){

                        cal = ((66 + (13.7 * binding.weight.text.toString().toDouble()) +
                                ((5 * binding.height.text.toString().toDouble()) - (6.8 * binding.age.text.toString().toDouble()))) * 1.55).roundToInt()

                        if(binding.objetivo.selectedItem.toString() == "Lose weight")
                        {
                            binding.result.text = "You must consume less than "+cal+" calories to lose weight"

                        }else if(binding.objetivo.selectedItem.toString() == "Gain weight"){

                            binding.result.text = "You must consume more than "+cal+" calories to gain weight"

                        }
                        else
                        {
                            binding.result.text = "You must consume "+cal+" calories to maintain your weight"
                        }

                    }
                    else
                    {
                        cal = ((66 + (13.7 * binding.weight.text.toString().toDouble()) +
                                ((5 * binding.height.text.toString().toDouble()) - (6.8 * binding.age.text.toString().toDouble()))) * 1.725).roundToInt()

                        if(binding.objetivo.selectedItem.toString() == "Lose weight")
                        {
                            binding.result.text = "You must consume less than "+cal+" calories to lose weight"

                        }else if(binding.objetivo.selectedItem.toString() == "Gain weight"){

                            binding.result.text = "You must consume more than "+cal+" calories to gain weight"

                        }
                        else
                        {
                            binding.result.text = "You must consume "+cal+" calories to maintain your weight"
                        }

                    }


                    val request = Volley.newRequestQueue(context)
                    val email = Firebase.auth.currentUser?.email.toString()
                    prefs.saveCalories(cal)

                    var url = "https://ivanurenda.000webhostapp.com/CalculateBIM.php?email=${email}&calories=${cal}"

                    url=url.replace(" ", "%20")
                    var stringRequest = StringRequest(Request.Method.GET, url, { response ->

                    }, { error ->

                    })
                    request.add(stringRequest)
                }
                objetivo.setSelection(0)
                genero.setSelection(0)
                fActividad.setSelection(0)
                binding.weight.text.clear()
                binding.height.text.clear()
                binding.age.text.clear()
            }
            else{
                if(binding.weight.text.toString().isEmpty() || binding.height.text.toString().isEmpty() || binding.age.toString().isEmpty()){
                    Toast.makeText(context, "Please enter a valid value", Toast.LENGTH_SHORT).show()
                }
                else if(binding.height.text.toString().toDouble() > 272){
                    Toast.makeText(context, "Please enter a valid height", Toast.LENGTH_SHORT).show()
                }else if(binding.age.text.toString().toDouble() > 100){
                    Toast.makeText(context, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                }
                else{
                    if(binding.fActividad.selectedItem.toString() == "Sedentary")
                    {
                        cal = ((655 + (9.6 * binding.weight.text.toString().toDouble()) +
                                ((1.8 * binding.height.text.toString().toDouble()) - (4.7 * binding.age.text.toString().toDouble()))) * 1.2).roundToInt()

                        if(binding.objetivo.selectedItem.toString() == "Lose weight")
                        {
                            binding.result.text = "You must consume less than "+cal+" calories to lose weight"

                        }else if(binding.objetivo.selectedItem.toString() == "Gain weight"){

                            binding.result.text = "You must consume more than "+cal+" calories to gain weight"

                        }
                        else
                        {
                            binding.result.text = "You must consume "+cal+" calories to maintain your weight"
                        }

                    }else if(binding.fActividad.selectedItem.toString() == "Little activity"){

                        cal = ((655 + (9.6 * binding.weight.text.toString().toDouble()) +
                                ((1.8 * binding.height.text.toString().toDouble()) - (4.7 * binding.age.text.toString().toDouble()))) * 1.375).roundToInt()

                        if(binding.objetivo.selectedItem.toString() == "Lose weight")
                        {
                            binding.result.text = "You must consume less than "+cal+" calories to lose weight"

                        }else if(binding.objetivo.selectedItem.toString() == "Gain weight"){

                            binding.result.text = "You must consume more than "+cal+" calories to gain weight"

                        }
                        else
                        {
                            binding.result.text = "You must consume "+cal+" calories to maintain your weight"
                        }

                    }else if(binding.fActividad.selectedItem.toString() == "Moderate activity"){

                        cal = ((655 + (9.6 * binding.weight.text.toString().toDouble()) +
                                ((1.8 * binding.height.text.toString().toDouble()) - (4.7 * binding.age.text.toString().toDouble()))) * 1.55).roundToInt()

                        if(binding.objetivo.selectedItem.toString() == "Lose weight")
                        {
                            binding.result.text = "You must consume less than "+cal+" calories to lose weight"

                        }else if(binding.objetivo.selectedItem.toString() == "Gain weight"){

                            binding.result.text = "You must consume more than "+cal+" calories to gain weight"

                        }
                        else
                        {
                            binding.result.text = "You must consume "+cal+" calories to maintain your weight"
                        }

                    }
                    else
                    {
                        cal = ((655 + (9.6 * binding.weight.text.toString().toDouble()) +
                                ((1.8 * binding.height.text.toString().toDouble()) - (4.7 * binding.age.text.toString().toDouble()))) * 1.725).roundToInt()

                        if(binding.objetivo.selectedItem.toString() == "Lose weight")
                        {
                            binding.result.text = "You must consume less than "+cal+" calories to lose weight"

                        }else if(binding.objetivo.selectedItem.toString() == "Gain weight"){

                            binding.result.text = "You must consume more than "+cal+" calories to gain weight"

                        }
                        else
                        {
                            binding.result.text = "You must consume "+cal+" calories to maintain your weight"
                        }

                    }

                    val request = Volley.newRequestQueue(context)
                    val email = Firebase.auth.currentUser?.email.toString()
                    prefs.saveCalories(cal)

                    var url = "https://ivanurenda.000webhostapp.com/CalculateBIM.php?email=${email}&calories=${cal}"

                    url=url.replace(" ", "%20")
                    var stringRequest = StringRequest(Request.Method.GET, url, { response ->

                    }, { error ->

                    })
                    request.add(stringRequest)
                }
                objetivo.setSelection(0)
                genero.setSelection(0)
                fActividad.setSelection(0)
                binding.weight.text.clear()
                binding.height.text.clear()
                binding.age.text.clear()
            }
            (activity as MainActivity?)?.setCaloriesGoal()
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}