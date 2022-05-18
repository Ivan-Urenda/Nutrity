package com.appNutrity.nutrity.apis

import com.appNutrity.nutrity.response.SearchRecipes

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIClient {
    @GET("/api/recipes/v2")
    fun getRecipesBySearch(@Query("type") type:String,
                            @Query("q") q:String,
                            @Query("app_id")idApp: String,
                            @Query("app_key")keyApp: String): Call<SearchRecipes>

}