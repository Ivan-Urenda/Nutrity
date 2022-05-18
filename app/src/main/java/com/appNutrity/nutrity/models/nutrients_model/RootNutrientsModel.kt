package com.appNutrity.nutrity.models.nutrients_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RootNutrientsModel (

    @SerializedName("PROCNT")
    @Expose()
    var protein: Nutrient,

    @SerializedName("CHOCDF")
    @Expose()
    var carbs: Nutrient,

    @SerializedName("FAT")
    @Expose()
    var fat: Nutrient,
)