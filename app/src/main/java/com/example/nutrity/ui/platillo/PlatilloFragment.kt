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
import androidx.lifecycle.ViewModelProvider
import com.example.nutrity.R
import com.squareup.picasso.Picasso


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
    private lateinit var listView: ListView
    private lateinit var lvingredients: ListView
    private lateinit var arrayAdapter: ArrayAdapter<*>
    private lateinit var ingredients: ArrayList<String>

    companion object {
        fun newInstance() = PlatilloFragment()
    }

    private lateinit var viewModel: PlatilloViewModel

    @SuppressLint("ClickableViewAccessibility")
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


        ingredients = arguments?.getStringArrayList("ingredients")!!

        recipeName.text = arguments?.getString("name")
        calories.text = arguments?.getString("calories")
        Picasso.get().load(arguments?.getString("image")).into(recetaImage)
        proteins.text = arguments?.getString("proteins")
        carbs.text = arguments?.getString("carbs")
        fat.text = arguments?.getString("fat")

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