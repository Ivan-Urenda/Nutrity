package com.example.nutrity

import com.example.nutrity.ui.caloriesCalculator.CaloriesCalculatorFragment
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CaloriesCalculatorUnitTest {
    @Test
    fun getCaloriesMessageTest() {
        val caloriesCalculator = CaloriesCalculatorFragment()
        var message: String

        try {
            message = caloriesCalculator.getCaloriesMessage("0", "0", "0", "0", "0", "0")
        } catch (e: Exception) {
            message = ""
        }
        assertEquals("", message)

        try {
            message = caloriesCalculator.getCaloriesMessage("40", "140", "18", "Women", "Sedentary", "Lose weight")
        } catch (e: Exception) {
            message = ""
        }
        assertEquals("You must consume less than 1448 calories to lose weight", message)

        try {
            message = caloriesCalculator.getCaloriesMessage("67", "165", "21", "Men", "Moderate activity", "Lose weight")
        } catch (e: Exception) {
            message = ""
        }
        assertEquals("You must consume less than 2582 calories to lose weight", message)
    }
}