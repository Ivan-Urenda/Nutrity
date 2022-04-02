package com.example.nutrity.json_files.recipe_model.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nutrity.R
import com.example.nutrity.models.RootObjectModel

class RecipeAdapter(private var recipes: ArrayList<RootObjectModel>): RecyclerView.Adapter<RecipeAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipes, parent, false)

        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.label.setText("Label\t"+recipes.get(position).recipeModel.label)
        holder.source.setText("Source\t"+recipes.get(position).recipeModel.source)
        holder.yeild.setText("Yeild\t"+recipes.get(position).recipeModel.yield)
        holder.calories.setText("Calories\t"+recipes.get(position).recipeModel.calories)
        holder.weight.setText("Weight\t"+recipes.get(position).recipeModel.totalWeight)
        Glide.with(holder.itemView.context).load(recipes.get(position).recipeModel.image).centerCrop().diskCacheStrategy(
            DiskCacheStrategy.ALL).into(holder.imageView)
    }

    override fun getItemCount(): Int {

        if (recipes != null){
            return recipes.size
        }

        return 0
    }

    class FoodViewHolder(view: View):RecyclerView.ViewHolder(view){

        var label: TextView = itemView.findViewById(R.id.text_label)
        var source: TextView = itemView.findViewById(R.id.text_src)
        var yeild: TextView = itemView.findViewById(R.id.text_yield)
        var calories: TextView = itemView.findViewById(R.id.text_calories)
        var weight: TextView = itemView.findViewById(R.id.text_weight)
        var imageView: ImageView = itemView.findViewById(R.id.ivRecipe)

    }

}