package com.appNutrity.nutrity.ui.caloriesCalculator

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
import com.appNutrity.nutrity.MainActivity
import com.appNutrity.nutrity.R
import com.appNutrity.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.appNutrity.nutrity.databinding.CalculatorFragmentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import kotlin.math.roundToInt

class CaloriesCalculatorFragment : Fragment() {

    private var _binding: CalculatorFragmentBinding? = null
    private lateinit var objetivo: Spinner
    private lateinit var genero: Spinner
    private lateinit var fActividad: Spinner
    var caloriesCalculation = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    fun getCaloriesMessage(
        weight: String,
        height: String,
        age: String,
        gender: String,
        activityLevel: String,
        goal: String
    ): String {
        var weightDouble: Double
        var heightDouble: Double
        var ageDouble: Double
        try {
            weightDouble = weight.toDouble()
            heightDouble = height.toDouble()
            ageDouble = age.toDouble()
        } catch (e: Exception) {
            if (weight.isEmpty()) {
                throw Exception("Please enter a valid weight value")
            }
            if (height.isEmpty()) {
                throw Exception("Please enter a valid height value")
            }
            if (age.isEmpty()) {
                throw Exception("Please enter a valid age value")
            }
            throw Exception("Please enter a valid number")
        }
        if (weightDouble > 250) {
            throw Exception("Please enter a valid weight value")
        }
        if (heightDouble > 250) {
            throw Exception("Please enter a valid height value")
        }
        if (ageDouble > 119) {
            throw Exception("Please enter a valid age value")
        }

        var activityCoefficient = when (activityLevel) {
            "Sedentary" -> 1.2
            "Little activity" -> 1.375
            "Moderate activity" -> 1.55
            "Intense activity" -> 1.725
            else -> throw Exception("Please enter a valid activity value")
        }.toDouble()
        caloriesCalculation = when (gender) {
            "Men" -> ((66 + (13.7 * weightDouble) +
                    ((5 * heightDouble) - (6.8 * ageDouble))) * activityCoefficient).roundToInt()
            "Women" ->
                ((655 + (9.6 * weightDouble) +
                        ((1.8 * heightDouble) - (4.7 * ageDouble))) * activityCoefficient).roundToInt()
            else -> throw Exception("Please enter a valid gender value")
        }
        return when (goal) {
            "Lose weight" ->
                "You must consume less than " + caloriesCalculation + " calories to lose weight"
            "Gain weight" ->
                "You must consume more than " + caloriesCalculation + " calories to gain weight"
            "Maintain weight" ->
                "You must consume " + caloriesCalculation + " calories to maintain your weight"
            else -> throw Exception("Please enter a valid objective value")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(CaloriesCalculatorViewModel::class.java)

        _binding = CalculatorFragmentBinding.inflate(inflater, container, false)
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
        val fActivities =
            listOf("Sedentary", "Little activity", "Moderate activity", "Intense activity")
        adaptador = ArrayAdapter(appContext, R.layout.spinner_items, fActivities)
        fActividad.adapter = adaptador

        binding.calculateButton.setOnClickListener {
            try {
                var caloriesMessage = getCaloriesMessage(
                    binding.weight.text.toString(),
                    binding.height.text.toString(),
                    binding.age.text.toString(),
                    binding.genero.selectedItem.toString(),
                    binding.fActividad.selectedItem.toString(),
                    binding.objetivo.selectedItem.toString()
                )
                binding.result.text = caloriesMessage
                saveValues()
            } catch (e: Exception) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }


        return root
    }

    private fun savePrefs() {
        prefs.saveCalories(caloriesCalculation)
        prefs.saveWeight(binding.weight.text.toString().toInt())
        prefs.saveHeight(binding.height.text.toString().toInt())
        prefs.saveAge(binding.age.text.toString().toInt())
        prefs.saveObjective(binding.objetivo.selectedItem.toString())
        (activity as MainActivity?)?.setCaloriesGoal()
    }

    private fun saveValues() {
        val request = Volley.newRequestQueue(context)
        val email = Firebase.auth.currentUser?.email.toString()
        savePrefs()

        val weight = binding.weight.text.toString()
        val height = binding.height.text.toString()
        val age = binding.age.text.toString()
        val objective = binding.objetivo.selectedItem.toString()

        var url =
            "https://ivanurenda.000webhostapp.com/CalculateCalories.php?email=${email}&calories=${caloriesCalculation}&weight=${weight}&height=${height}&age=${age}&objective=${objective}"

        url = url.replace(" ", "%20")
        var stringRequest = StringRequest(Request.Method.POST, url, { _ -> clear() }, { _ -> })
        request.add(stringRequest)
    }

    private fun clear() {
        objetivo.setSelection(0)
        genero.setSelection(0)
        fActividad.setSelection(0)
        binding.weight.text.clear()
        binding.height.text.clear()
        binding.age.text.clear()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}