package com.example.nutrity.models

import com.example.nutrity.models.image_model.RootImageModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RecipeModel (
    var label: String,
    var image: String,
    var source: String,
    var calories: Float,
    var ingredientLines: ArrayList<String>,
    @SerializedName("images")
    @Expose()
    var rootImageModel: RootImageModel
    )