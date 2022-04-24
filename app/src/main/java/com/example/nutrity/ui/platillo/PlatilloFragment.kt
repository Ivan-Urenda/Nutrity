package com.example.nutrity.ui.platillo

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.nutrity.R
import com.example.nutrity.ui.calorias.CaloriasFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*


class PlatilloFragment : Fragment(), SensorEventListener {

    var sensorManager: SensorManager?= null
    var sensor: Sensor?= null
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
        calories.text = arguments?.getString("calories")+" kcal"
        Picasso.get().load(arguments?.getString("image")).into(recetaImage)
        proteins.text = arguments?.getString("proteins")+" g"
        carbs.text = arguments?.getString("carbs")+" g"
        fat.text = arguments?.getString("fat")+" g"

        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ingredients)
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

            GlobalScope.launch(Dispatchers.Main) {
                var calories: Int? = null
                var proteins: Int? = null
                var carbs: Int? = null
                var fats: Int? = null

                withContext(Dispatchers.IO){
                    Firebase.firestore.collection("users")
                        .document(Firebase.auth.currentUser?.email.toString()).get()
                        .addOnCompleteListener { document ->
                            calories = document.result.get("day").toString().toInt()
                            proteins = document.result.get("proteins").toString().toInt()
                            carbs = document.result.get("carbs").toString().toInt()
                            fats = document.result.get("fats").toString().toInt()
                        }
                }

                delay(1000)
                val calString = arguments?.getString("calories")
                val proteinString = arguments?.getString("proteins")
                val carbsString = arguments?.getString("carbs")
                val fatsString = arguments?.getString("fat")

                progress = calString?.toInt() !!+ calories!!
                progressProteins = proteinString?.toInt() !!+ proteins!!
                progressCarbs = carbsString?.toInt() !!+ carbs!!
                progressFats = fatsString?.toInt() !!+ fats!!


                Firebase.firestore.collection("users")
                    .document(Firebase.auth.currentUser?.email.toString()).update(
                        mutableMapOf<String, Any>(
                            "day" to progress.toString(),
                            "proteins" to progressProteins.toString(),
                            "carbs" to progressCarbs.toString(),
                            "fats" to progressFats.toString()
                        )
                    )

                Toast.makeText(context, "Dish added to your day", Toast.LENGTH_SHORT).show()

                Firebase.firestore.collection("users")
                    .document(Firebase.auth.currentUser?.email.toString()).collection("recipesAdded")
                    .document(arguments?.getString("name").toString()).set(
                        hashMapOf(
                            "void" to 0
                        )
                    )
            }
        }

        return root
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
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
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