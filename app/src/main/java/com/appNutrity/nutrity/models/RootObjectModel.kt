package com.appNutrity.nutrity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RootObjectModel (
    @SerializedName("recipe")
    @Expose()
    var recipeModel: RecipeModel
        )