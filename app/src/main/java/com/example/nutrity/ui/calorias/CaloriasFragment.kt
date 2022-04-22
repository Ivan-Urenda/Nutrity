package com.example.nutrity.ui.calorias

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nutrity.databinding.FragmentCaloriasBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class CaloriasFragment : Fragment() {

    private var _binding: FragmentCaloriasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var day: String
    private lateinit var objective: String
    private lateinit var progressBar: ProgressBar
    private var recipes = arrayListOf<String>()
    private lateinit var arrayAdapter: ArrayAdapter<*>

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(CaloriasViewModel::class.java)

        _binding = FragmentCaloriasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressBar = binding.pBar
        progressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#38B745"))

        GlobalScope.launch(Dispatchers.Main) {

            withContext(Dispatchers.IO){
                Firebase.firestore.collection("users")
                    .document(Firebase.auth.currentUser?.email.toString()).get()
                    .addOnCompleteListener { document ->
                        day = document.result.get("day").toString()
                        objective = document.result.get("calories").toString()
                    }
                Firebase.firestore.collection("users")
                    .document(Firebase.auth.currentUser?.email.toString()).collection("recipesAdded").get()
                    .addOnCompleteListener {
                        for (document in it.result){
                            recipes.add(document.id)
                        }
                    }
            }
            delay(1000)

            progressBar.max = objective.toInt()
            ObjectAnimator.ofInt(progressBar, "progress", day.toInt())
                .setDuration(2000)
                .start()

            binding.actualCalories.text = day
            binding.objetivoCalories.text = objective

            arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recipes)
            binding.lvRecipes.adapter = arrayAdapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}