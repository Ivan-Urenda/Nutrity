package com.appNutrity.nutrity.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.appNutrity.nutrity.R
import com.appNutrity.nutrity.models.RootObjectModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class RecipeAdapter(private var recipes: ArrayList<RootObjectModel>) :
    RecyclerView.Adapter<RecipeAdapter.FoodViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipes, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        holder.label.text = recipes[position].recipeModel.label
        holder.source.text = recipes[position].recipeModel.source
        holder.yield.text = recipes[position].recipeModel.yield.toString() + " portions"
        holder.calories.text =
            "" + (recipes[position].recipeModel.calories.roundToInt() / recipes[position].recipeModel.yield.toString()
                .toInt()) + " kcal per portion"
        Glide.with(holder.itemView.context).load(recipes[position].recipeModel.image).centerCrop()
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).into(holder.imageView)
        holder.cardView.setOnClickListener {
            val navController = Navigation.findNavController(
                (holder.context) as Activity,
                R.id.nav_host_fragment_content_main
            )
            val bundle = bundleOf(
                "name" to holder.label.text.toString(),
                "calories" to (recipes[position].recipeModel.calories.roundToInt() / recipes[position].recipeModel.yield.toString()
                    .toInt()).toString(),
                "proteins" to (recipes[position].recipeModel.rootNutrientsModel.protein.quantity.roundToInt() / recipes[position].recipeModel.yield.toString()
                    .toInt()).toString(),
                "carbs" to (recipes[position].recipeModel.rootNutrientsModel.carbs.quantity.roundToInt() / recipes[position].recipeModel.yield.toString()
                    .toInt()).toString(),
                "fat" to (recipes[position].recipeModel.rootNutrientsModel.fat.quantity.roundToInt() / recipes[position].recipeModel.yield.toString()
                    .toInt()).toString(),
                "ingredients" to recipes[position].recipeModel.ingredientLines,
                "image" to recipes[position].recipeModel.image
            )
            navController.navigate(R.id.platilloFragment, bundle)
        }
    }

    override fun getItemCount(): Int {

        if (recipes != null) {
            return recipes.size
        }

        return 0
    }

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var label: TextView = itemView.findViewById(R.id.text_label)
        var source: TextView = itemView.findViewById(R.id.text_src)
        var calories: TextView = itemView.findViewById(R.id.text_calories)
        var yield: TextView = itemView.findViewById(R.id.text_yield)
        var imageView: ImageView = itemView.findViewById(R.id.ivRecipe)
        var cardView: CardView = itemView.findViewById(R.id.Cardv)
        var context: Context = view.context

    }

}