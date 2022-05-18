package com.example.nutrity.ui.calorias

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nutrity.R
import com.example.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.example.nutrity.databinding.FragmentCaloriasBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class CaloriasFragment : Fragment() {

    private var _binding: FragmentCaloriasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var progress: String
    private lateinit var objective: String
    private lateinit var proteins: String
    private lateinit var carbs: String
    private lateinit var fats: String
    private lateinit var progressBar: ProgressBar
    private lateinit var dialog: AlertDialog
    private lateinit var recipes: ArrayList<String>
    private lateinit var arrayAdapter: ArrayAdapter<*>

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ClickableViewAccessibility")
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

        if (prefs.getCalories() == 0) {
            alert("You should first calculate your target calories before seeing your progress for the day.")
        } else {
            loadData()
        }

        binding.startDayButton.setOnClickListener {alertDialog()}

        //Allows you to scroll through the list of recipes
        binding.lvRecipes.setOnTouchListener { _, event ->
            binding.cScrollView.requestDisallowInterceptTouchEvent(true)
            when (event.actionMasked) {
                MotionEvent.ACTION_UP -> binding.cScrollView.requestDisallowInterceptTouchEvent(
                    false
                )
            }
            false
        }

        return root
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun loadData() {
        GlobalScope.launch(Dispatchers.Main) {

            setData()

            withContext(Dispatchers.IO) {

                val request = Volley.newRequestQueue(context)
                val email = Firebase.auth.currentUser?.email.toString()

                var url = "https://ivanurenda.000webhostapp.com/SearchRecipes.php?email=${email}"
                url = url.replace(" ", "%20")
                var stringRequest = StringRequest(Request.Method.GET, url, { response ->
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = JSONObject(jsonArray.getString(i))
                        recipes.add(jsonObject.get("recipeName").toString())
                    }
                    setRecipes()
                }, { error ->

                })
                request.add(stringRequest)
            }
        }
    }

    //Shows you the nutrients of your day
    private fun setData() {
        recipes = arrayListOf<String>()
        progress = prefs.getProgress().toString()
        objective = prefs.getCalories().toString()
        proteins = prefs.getProteins().toString()
        carbs = prefs.getCarbs().toString()
        fats = prefs.getFats().toString()

        binding.actualCalories.text = progress + " kcal"
        binding.objetivoCalories.text = objective + " kcal"
        binding.proteins.text = "$proteins g"
        binding.carbs.text = "$carbs g"
        binding.fats.text = "$fats g"

        progressBar.max = objective.toInt()
        ObjectAnimator.ofInt(progressBar, "progress", progress.toInt())
            .setDuration(2000)
            .start()
    }

    //Shows you the recipes of your day
    private fun setRecipes() {
        arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recipes)
        binding.lvRecipes.adapter = arrayAdapter
    }

    //Shows you an alert before you start your day
    private fun alertDialog() {

        AlertDialog.Builder(context).apply {
            setTitle("Alert")
            setMessage("Are you sure you want to start your day? Your previous progress will be reset.")
            setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                deleteData()
            }
            setNegativeButton("No", null)
        }.show()
    }

    //Alerts you that you don't have a calories goal
    private fun alert(message: String) {

        AlertDialog.Builder(context).apply {
            setTitle("Nutrity")
            setMessage(message)
            setPositiveButton("Acept") { _: DialogInterface, _: Int ->
            }
        }.show()
    }

    //Show the Loading Screen
    private fun loadingDialog() {

        dialog = AlertDialog.Builder(context).apply {
            setView(R.layout.loading_item)
        }.show()
    }

    //Hide Loading Screen
    fun isDismiss() {
        dialog.dismiss()
    }

    private fun deleteData() {

        loadingDialog()

        prefs.saveProgress(0)
        prefs.saveProteins(0)
        prefs.saveCarbs(0)
        prefs.saveFats(0)

        GlobalScope.launch(Dispatchers.Main) {

            withContext(Dispatchers.IO) {

                //Reset all nutrients values
                val request = Volley.newRequestQueue(context)
                val email = Firebase.auth.currentUser?.email.toString()

                var url =
                    "https://ivanurenda.000webhostapp.com/Reset.php?email=${email}&calories=${objective}"

                url = url.replace(" ", "%20")
                var stringRequest = StringRequest(Request.Method.GET, url, { response ->

                }, { error ->

                    Toast.makeText(context, "An error has occurred, please try again later", Toast.LENGTH_SHORT).show()
                })
                request.add(stringRequest)

                //Delete all recipes
                url = "https://ivanurenda.000webhostapp.com/DeleteRecipes.php?email=${email}"

                url = url.replace(" ", "%20")
                stringRequest = StringRequest(Request.Method.GET, url, { response ->

                    alert("Have a nice day :)")
                }, { error ->

                    Toast.makeText(context, "An error has occurred, please try again later", Toast.LENGTH_SHORT).show()
                })
                request.add(stringRequest)

            }
            delay(1000)
            setData()
            setRecipes()
            isDismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}