package com.example.nutrity.response

import com.example.nutrity.models.RootObjectModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SearchRecipes {

    private var from: Int = 0
    private var to: Int = 0
    private var count: Int = 0
    @SerializedName("hits")
    @Expose()
    private lateinit var foodRecipes: ArrayList<RootObjectModel>


    public fun getFoodRecipes(): ArrayList<RootObjectModel>{

        return foodRecipes
    }

    public override fun toString(): String {

        return "Recipes{"+"Recipes=\t"+foodRecipes + "from = \t"+from+"to=\t"+to+"totalCount=\t"+count +"}"
    }
}