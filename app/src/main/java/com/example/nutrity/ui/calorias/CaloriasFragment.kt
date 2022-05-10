package com.example.nutrity.ui.calorias

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
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
import java.util.ArrayList

class CaloriasFragment : Fragment() {

    private var _binding: FragmentCaloriasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var day: String
    private lateinit var objective: String
    private lateinit var proteins: String
    private lateinit var carbs: String
    private lateinit var fats: String
    private val db = Firebase.firestore
    private lateinit var progressBar: ProgressBar
    private lateinit var recipes: ArrayList<String>
    private lateinit var arrayAdapter: ArrayAdapter<*>

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

        loadData()

        binding.startDayButton.setOnClickListener {

            alertDialog()
        }

        return root
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun loadData() {
        GlobalScope.launch(Dispatchers.Main) {
            recipes = arrayListOf<String>()

            withContext(Dispatchers.IO) {
                db.collection("users")
                    .document(Firebase.auth.currentUser?.email.toString()).get()
                    .addOnCompleteListener { document ->
                        day = document.result.get("day").toString()
                        objective = document.result.get("calories").toString()
                        proteins = document.result.get("proteins").toString()
                        carbs = document.result.get("carbs").toString()
                        fats = document.result.get("fats").toString()

                    }
                db.collection("users")
                    .document(Firebase.auth.currentUser?.email.toString())
                    .collection("recipesAdded").get()
                    .addOnCompleteListener {
                        for (document in it.result) {
                            recipes.add(document.id)
                        }
                    }
            }
            delay(1000)

            progressBar.max = objective.toInt()
            ObjectAnimator.ofInt(progressBar, "progress", day.toInt())
                .setDuration(2000)
                .start()

            binding.actualCalories.text = day + " kcal"
            binding.objetivoCalories.text = objective + " kcal"
            binding.proteins.text = "$proteins g"
            binding.carbs.text = "$carbs g"
            binding.fats.text = "$fats g"

            arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recipes)
            binding.lvRecipes.adapter = arrayAdapter
        }
    }

    private fun alertDialog() {

        AlertDialog.Builder(context).apply {
            setTitle("Alert")
            setMessage("Are you sure you want to start your day? Your previous progress will be reset.")
            setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                deleteData()
                loadData()
            }
            setNegativeButton("No", null)
        }.show()

    }

    private fun deleteData() {
        recipes.forEach {
            db.collection("users")
                .document(Firebase.auth.currentUser?.email.toString()).collection("recipesAdded")
                .document(it).delete()
        }
        db.collection("users")
            .document(Firebase.auth.currentUser?.email.toString()).update(
                mutableMapOf<String, Any>(
                    "day" to 0,
                    "proteins" to 0,
                    "carbs" to 0,
                    "fats" to 0
                )
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}