package com.example.nutrity.json_files.recipe_model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
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
        holder.label.setText(recipes.get(position).recipeModel.label)
        holder.source.setText("Source: "+recipes.get(position).recipeModel.source)
        holder.yeild.setText("Portions: "+recipes.get(position).recipeModel.yield)
        holder.calories.setText("Calories: "+recipes.get(position).recipeModel.calories+" kcal")
        holder.weight.setText("Weight: "+recipes.get(position).recipeModel.totalWeight+" g")
        Glide.with(holder.itemView.context).load(recipes.get(position).recipeModel.image).centerCrop().diskCacheStrategy(
            DiskCacheStrategy.ALL).into(holder.imageView)
        holder.cardView.setOnClickListener {  Toast.makeText(holder.context, "Card: "+position, Toast.LENGTH_SHORT).show() }
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
        var cardView: CardView = itemView.findViewById(R.id.Cardv)
        var context: Context = view.context

    }

}