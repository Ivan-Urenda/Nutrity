package com.example.nutrity.ui.imc

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrity.R
import com.example.nutrity.databinding.ActivityImcBinding
import kotlin.math.pow

class IMCActivity : AppCompatActivity(){

    private lateinit var binding: ActivityImcBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calculate_imc = binding.calculateButton
        val weight_value = binding.weight?.text.toString()
        val height_value = binding.height?.text.toString()

        val weightValue: Double = weight_value.toDouble()
        val heightValue: Double = height_value.toDouble() / 100

        val imc = weightValue / heightValue.pow(2)

        calculate_imc?.setOnClickListener{
            if(imc < 16){
                binding.result?.text = "Resultado\n\n" + imc + "\n\nDelgadez severa"
            }
            else if(imc < 18.5){
                binding.result?.text = "Resultado\n\n" + imc + "\n\nBajo peso"
            }
            else if(imc in 18.5..24.9){
                binding.result?.text = "Resultado\n\n" + imc + "\n\nNormal"
            }
            else if(imc >= 25){
                binding.result?.text = "Resultado\n\n" + imc + "\n\nSobrepeso"
            }
            else{
                binding.result?.text = "Resultado\n\n" + imc + "\n\nObesidad"
            }
        }
    }

}