package com.caterer.puja.data.repo

import com.caterer.puja.data.model.Dish
import com.caterer.puja.data.model.Ingredient

interface CateringRepository {
    fun getDishes(): List<Dish>
    fun getIngredients(): List<Ingredient>
}

