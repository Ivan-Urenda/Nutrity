package com.example.nutrity.ui.platillo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nutrity.R
import com.example.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject


class PlatilloFragment : Fragment(), SensorEventListener {

    var sensorManager: SensorManager? = null
    var sensor: Sensor? = null
    private lateinit var recetaImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var calories: TextView
    private lateinit var proteins: TextView
    private lateinit var carbs: TextView
    private lateinit var fat: TextView
    private lateinit var ScrollView: ScrollView
    private lateinit var addBtn: Button
    private lateinit var lvingredients: ListView
    private lateinit var arrayAdapter: ArrayAdapter<*>
    private lateinit var ingredients: ArrayList<String>
    private var progress: Int? = null
    private var progressProteins: Int? = null
    private var progressCarbs: Int? = null
    private var progressFats: Int? = null
    private lateinit var dialog: AlertDialog

    companion object {
        fun newInstance() = PlatilloFragment()
    }

    private lateinit var viewModel: PlatilloViewModel

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.platillo_fragment, container, false)

        recetaImage = root.findViewById(R.id.platilloImage)
        recipeName = root.findViewById(R.id.RecipeName)
        calories = root.findViewById(R.id.calories)
        proteins = root.findViewById(R.id.proteins)
        carbs = root.findViewById(R.id.carbs)
        fat = root.findViewById(R.id.fat)
        lvingredients = root.findViewById(R.id.lvIngredients)
        ScrollView = root.findViewById(R.id.miScrollView)
        addBtn = root.findViewById(R.id.add_button)


        ingredients = arguments?.getStringArrayList("ingredients")!!

        recipeName.text = arguments?.getString("name")
        calories.text = arguments?.getString("calories") + " kcal"
        Picasso.get().load(arguments?.getString("image")).into(recetaImage)
        proteins.text = arguments?.getString("proteins") + " g"
        carbs.text = arguments?.getString("carbs") + " g"
        fat.text = arguments?.getString("fat") + " g"

        arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ingredients)
        lvingredients.adapter = arrayAdapter

        sensorManager = context?.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        lvingredients.setOnTouchListener { _, event ->
            ScrollView.requestDisallowInterceptTouchEvent(true)
            when (event.actionMasked) {
                MotionEvent.ACTION_UP -> ScrollView.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        addBtn.setOnClickListener {
            loadingDialog()
            GlobalScope.launch(Dispatchers.Main) {

                withContext(Dispatchers.IO) {

                    val request = Volley.newRequestQueue(context)
                    val email = Firebase.auth.currentUser?.email.toString()

                    var url = "https://ivanurenda.000webhostapp.com/UserData.php?email=${email}"
                    url=url.replace(" ", "%20")
                    val stringRequest = StringRequest(Request.Method.GET, url, { response ->

                        val jsonArray = JSONArray(response)
                        val jsonObject = JSONObject(jsonArray.getString(0))
                        calculateNutrients(jsonObject)
                        updateNutrients()
                        AddRecipe()
                        isDismiss()
                        acceptDialog()
                    }, { error ->

                    })
                    request.add(stringRequest)
                }
            }
        }

        return root
    }

    private fun calculateNutrients(jsonObject: JSONObject){
        val calString = arguments?.getString("calories")
        val proteinString = arguments?.getString("proteins")
        val carbsString = arguments?.getString("carbs")
        val fatsString = arguments?.getString("fat")

        progress = calString?.toInt()!! + jsonObject.get("progress").toString().toInt()
        progressProteins = proteinString?.toInt()!! + jsonObject.get("proteins").toString().toInt()
        progressCarbs = carbsString?.toInt()!! + jsonObject.get("carbs").toString().toInt()
        progressFats = fatsString?.toInt()!! + jsonObject.get("fats").toString().toInt()
    }

    private fun updateNutrients(){

        prefs.saveProgress(progress!!)
        prefs.saveProteins(progressProteins!!)
        prefs.saveCarbs(progressCarbs!!)
        prefs.saveFats(progressFats!!)

        try {
            val request = Volley.newRequestQueue(context)
            val email = Firebase.auth.currentUser?.email.toString()

            var url = "https://ivanurenda.000webhostapp.com/UpdateNutrients.php?email=${email}&progress=${progress}"+
                    "&proteins=${progressProteins}&carbs=${progressCarbs}&fats=${progressFats}"

            url=url.replace(" ", "%20")
            val stringRequest = StringRequest(Request.Method.POST, url, { response ->

            }, { error ->

                Toast.makeText(context, "Error!! Nutrients could not be updated", Toast.LENGTH_SHORT).show()
            })
            request.add(stringRequest)
        }catch (e: Exception){
            Toast.makeText(context, "Error!! Nutrients could not be updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun AddRecipe(){

        try {
            val request = Volley.newRequestQueue(context)
            val email = Firebase.auth.currentUser?.email.toString()

            var url = "https://ivanurenda.000webhostapp.com/AddRecipes.php?email=${email}&recipeName=${arguments?.getString("name").toString()}"

            url=url.replace(" ", "%20")
            val stringRequest = StringRequest(Request.Method.POST, url, { response ->

            }, { error ->

                Toast.makeText(context, "Error!! Failed to add recipe", Toast.LENGTH_SHORT).show()
            })
            request.add(stringRequest)
        }catch (e: Exception){
            Toast.makeText(context, "Error!! Failed to add recipe", Toast.LENGTH_SHORT).show()
        }

    }

    private fun acceptDialog(){
        try {
            AlertDialog.Builder(context).apply {
                setTitle("Nutrity")
                setMessage("Your recipe was added succesfully.")
                setPositiveButton("Acept") { _: DialogInterface, _: Int ->
                }
            }.show()
        }catch (e: Exception){

        }
    }

    private fun loadingDialog() {

        dialog = AlertDialog.Builder(context).apply {
            setView(R.layout.loading_item)
        }.show()
    }

    private fun isDismiss() {
        dialog.dismiss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlatilloViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0]
            val upDown = event.values[1]

            recetaImage.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
                translationY = upDown * 10
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

}