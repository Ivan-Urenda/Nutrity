package com.appNutrity.nutrity.dataPersistence

import android.content.Context
import android.content.SharedPreferences

class Prefs(val context: Context) {

    private val SHARED_NUTRIENTS= "Mydtb"
    private val SHARED_LOGGED = "logged"
    private val SHARED_PROGRESS = "progress"
    private val SHARED_CALORIES = "calories"
    private val SHARED_PROTEINS = "proteins"
    private val SHARED_CARBS = "carbs"
    private val SHARED_FATS = "fats"
    private val SHARED_USERNAME = "username"
    private val SHARED_URI = "uri"
    private val SHARED_FIRST = "first"
    private val SHARED_LAST = "last"
    private val SHARED_WEIGHT = "weight"
    private val SHARED_HEIGHT = "height"
    private val SHARED_AGE = "age"
    private val SHARED_OBJECTIVE = "objective"

    private val storage: SharedPreferences = context.getSharedPreferences(SHARED_NUTRIENTS, 0)

    fun saveLogged(logged: Boolean) {
        storage.edit().putBoolean(SHARED_LOGGED, logged).apply()
    }

    fun getLogged(): Boolean {
        return storage.getBoolean(SHARED_LOGGED, false)
    }

    fun saveProgress(progress: Int) {
        storage.edit().putInt(SHARED_PROGRESS, progress).apply()
    }

    fun getProgress(): Int {
        return storage.getInt(SHARED_PROGRESS, 0)
    }

    fun saveCalories(calories: Int) {
        storage.edit().putInt(SHARED_CALORIES, calories).apply()
    }

    fun getCalories(): Int {
        return storage.getInt(SHARED_CALORIES, 0)
    }

    fun saveProteins(proteins: Int) {
        storage.edit().putInt(SHARED_PROTEINS, proteins).apply()
    }

    fun getProteins(): Int {
        return storage.getInt(SHARED_PROTEINS, 0)
    }

    fun saveCarbs(carbs: Int) {
        storage.edit().putInt(SHARED_CARBS, carbs).apply()
    }

    fun getCarbs(): Int {
        return storage.getInt(SHARED_CARBS, 0)
    }

    fun saveFats(fats: Int) {
        storage.edit().putInt(SHARED_FATS, fats).apply()
    }

    fun getFats(): Int {
        return storage.getInt(SHARED_FATS, 0)
    }

    fun saveUsername(username: String) {
        storage.edit().putString(SHARED_USERNAME, username).apply()
    }

    fun getUsername(): String {
        return storage.getString(SHARED_USERNAME, "User")!!
    }

    fun saveUri(Uri: String) {
        storage.edit().putString(SHARED_URI, Uri).apply()
    }

    fun getUri(): String {
        return storage.getString(SHARED_URI, "")!!
    }

    fun saveFirstName(firstName: String) {
        storage.edit().putString(SHARED_FIRST, firstName).apply()
    }

    fun getFirstName(): String {
        return storage.getString(SHARED_FIRST, "")!!
    }

    fun saveLastName(lastName: String) {
        storage.edit().putString(SHARED_LAST, lastName).apply()
    }

    fun getLastName(): String {
        return storage.getString(SHARED_LAST, "")!!
    }

    fun saveWeight(weight: Int) {
        storage.edit().putInt(SHARED_WEIGHT, weight).apply()
    }

    fun getWeight(): Int {
        return storage.getInt(SHARED_WEIGHT, 0)
    }

    fun saveHeight(height: Int) {
        storage.edit().putInt(SHARED_HEIGHT, height).apply()
    }

    fun getHeight(): Int {
        return storage.getInt(SHARED_HEIGHT, 0)
    }

    fun saveAge(age: Int) {
        storage.edit().putInt(SHARED_AGE, age).apply()
    }

    fun getAge(): Int {
        return storage.getInt(SHARED_AGE, 0)
    }

    fun saveObjective(objective: String) {
        storage.edit().putString(SHARED_OBJECTIVE, objective).apply()
    }

    fun getObjective(): String {
        return storage.getString(SHARED_OBJECTIVE, "")!!
    }

    fun wipe() {
        storage.edit().clear().apply()
    }

}