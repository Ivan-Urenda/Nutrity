package com.example.nutrity.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nutrity.MainActivity
import com.example.nutrity.R
import com.example.nutrity.models.RootObjectModel
import com.example.nutrity.ui.platillo.PlatilloFragment

class RecipeAdapter(private var recipes: ArrayList<RootObjectModel>): RecyclerView.Adapter<RecipeAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipes, parent, false)

        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.label.setText(recipes.get(position).recipeModel.label)
        holder.source.setText(recipes.get(position).recipeModel.source)
        holder.calories.setText(""+recipes.get(position).recipeModel.calories+" kcal")
        Glide.with(holder.itemView.context).load(recipes.get(position).recipeModel.image).centerCrop().diskCacheStrategy(
            DiskCacheStrategy.ALL).into(holder.imageView)
        holder.cardView.setOnClickListener {
            val navController= Navigation.findNavController((holder.context) as Activity, R.id.nav_host_fragment_content_main)
            val bundle = bundleOf("name" to holder.label.text.toString(), "calories" to holder.calories.text.toString(), "image" to recipes.get(position).recipeModel.image)
            navController.navigate(R.id.platilloFragment, bundle)
        }
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
        var calories: TextView = itemView.findViewById(R.id.text_calories)
        var imageView: ImageView = itemView.findViewById(R.id.ivRecipe)
        var cardView: CardView = itemView.findViewById(R.id.Cardv)
        var context: Context = view.context

    }

}