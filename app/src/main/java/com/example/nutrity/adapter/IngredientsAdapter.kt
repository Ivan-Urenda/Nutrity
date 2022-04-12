package com.example.nutrity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrity.R

class IngredientsAdapter(private var ingredients: ArrayList<String>): RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredients, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.ingredient.setText(ingredients.get(position))
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    class IngredientViewHolder(view: View):RecyclerView.ViewHolder(view) {

        var ingredient: TextView = itemView.findViewById(R.id.Ingredient)

    }
}